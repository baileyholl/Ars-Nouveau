package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.entity.ChangeableBehavior;
import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.item.NBTComponent;
import com.hollingsworth.arsnouveau.api.registry.BehaviorRegistry;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SummonUtil;
import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.advancement.ANCriteriaTriggers;
import com.hollingsworth.arsnouveau.common.compat.PatchouliHandler;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.entity.debug.DebugEvent;
import com.hollingsworth.arsnouveau.common.entity.debug.EntityDebugger;
import com.hollingsworth.arsnouveau.common.entity.debug.IDebugger;
import com.hollingsworth.arsnouveau.common.entity.debug.IDebuggerProvider;
import com.hollingsworth.arsnouveau.common.entity.goal.AvoidEntityGoalMC;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyTransportBehavior;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.UntamedFindItem;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.MinecoloniesAdvancedPathNavigate;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.MovementHandler;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.PathingStuckHandler;
import com.hollingsworth.arsnouveau.common.items.data.StarbuncleCharmData;
import com.hollingsworth.arsnouveau.common.items.summon_charms.StarbuncleCharm;
import com.hollingsworth.arsnouveau.common.network.ITagSyncable;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSyncTag;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import com.hollingsworth.arsnouveau.setup.reward.Rewards;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

public class Starbuncle extends PathfinderMob implements GeoEntity, IDecoratable, IDispellable, ITooltipProvider, IWandable, IDebuggerProvider, ITagSyncable {


    @Deprecated
    public enum StarbuncleGoalState {
        FORAGING,
        HUNTING_ITEM,
        TAKING_ITEM,
        STORING_ITEM,
        RESTING,
        NONE
    }

    public EntityDebugger debugger = new EntityDebugger(this);
    public StarbuncleGoalState goalState = StarbuncleGoalState.NONE;
    private MinecoloniesAdvancedPathNavigate pathNavigate;

    public static final EntityDataAccessor<Boolean> TAMED = SynchedEntityData.defineId(Starbuncle.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> COLOR = SynchedEntityData.defineId(Starbuncle.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> PATH_BLOCK = SynchedEntityData.defineId(Starbuncle.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> BEHAVIOR_KEY = SynchedEntityData.defineId(Starbuncle.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<ItemStack> HEAD_COSMETIC = SynchedEntityData.defineId(Starbuncle.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<CompoundTag> BEHAVIOR_TAG = SynchedEntityData.defineId(Starbuncle.class, EntityDataSerializers.COMPOUND_TAG);
    private int backOff; // Used to stop inventory store/take spam when chests are full or empty.

    private int bedBackoff;

    public int tamingTime;
    private int lastAABBCalc;
    private AABB cachedAAB;

    public BlockPos jukeboxPos;
    public boolean partyCarby;
    public PathNavigation minecraftPathNav;
    public StarbuncleCharmData.Mutable data = new StarbuncleCharmData.Mutable();
    public ChangeableBehavior dynamicBehavior = new StarbyTransportBehavior(this, new CompoundTag());
    public boolean canSleep;
    public boolean sleeping;
    AnimatableInstanceCache manager = GeckoLibUtil.createInstanceCache(this);

    public Starbuncle(EntityType<? extends Starbuncle> entityCarbuncleEntityType, Level world) {
        super(entityCarbuncleEntityType, world);
        dynamicBehavior = new StarbyTransportBehavior(this, new CompoundTag());
        reloadGoals();
        this.moveControl = new MovementHandler(this);
        this.setPathfindingMalus(PathType.DANGER_OTHER, 0.0F);
        this.setPathfindingMalus(PathType.DAMAGE_OTHER, 0.0F);
    }

    public Starbuncle(Level world, boolean tamed) {
        this(ModEntities.STARBUNCLE_TYPE.get(), world);
        this.setTamed(tamed);
    }

    @Override
    public @NotNull MinecoloniesAdvancedPathNavigate getNavigation() {
        if (this.pathNavigate == null) {
            this.pathNavigate = new MinecoloniesAdvancedPathNavigate(this, this.level);
            this.minecraftPathNav = this.navigation;
            this.navigation = pathNavigate;
            this.pathNavigate.setCanFloat(true);
            this.pathNavigate.setSwimSpeedFactor(2.0);
            this.pathNavigate.getPathingOptions().setEnterDoors(true);
            this.pathNavigate.getPathingOptions().setCanOpenDoors(true);
            if (this.isTamed()) {
                this.pathNavigate.setStuckHandler(PathingStuckHandler.createStuckHandler().withTeleportOnFullStuck().withTeleportSteps(5));
            }
            this.pathNavigate.getPathingOptions().setCanFitInOneCube(true);
            this.pathNavigate.getPathingOptions().onPathCost = 0.1D;
            this.pathNavigate.getPathingOptions().withRoadState(this::isOnRoad);

        }
        return pathNavigate;
    }

    public Boolean isOnRoad(BlockState state) {
        return state.getBlock() instanceof DirtPathBlock || (data.pathBlock != null && data.pathBlock == state.getBlock());
    }

    public void setBehavior(ChangeableBehavior behavior) {
        this.dynamicBehavior = behavior;
        this.data.behaviorKey = behavior.getRegistryName();
        getEntityData().set(Starbuncle.BEHAVIOR_TAG, dynamicBehavior.toTag(new CompoundTag()));
        getEntityData().set(Starbuncle.BEHAVIOR_KEY, behavior.getRegistryName().toString());
        reloadGoals();
        syncBehavior();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animatableManager) {
        animatableManager.add(new AnimationController<>(this, "walkController", 1, (event) -> {
            if (event.isMoving() || (level.isClientSide && PatchouliHandler.isPatchouliWorld())) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("run"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        animatableManager.add(new AnimationController<>(this, "danceController", 1, (event) -> {
            if ((!this.isTamed() && getHeldStack().is(Tags.Items.NUGGETS_GOLD)) || (this.partyCarby && this.jukeboxPos != null && BlockUtil.distanceFrom(position, jukeboxPos) <= 8)) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("dance"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        animatableManager.add(new AnimationController<>(this, "sleepController", 1, (event) -> {
            boolean shouldSleep = canSleep || (this.getVehicle() instanceof Starbuncle vehicle && vehicle.sleeping);
            if (!event.isMoving() && shouldSleep) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("resting"));
                sleeping = true;
                return PlayState.CONTINUE;
            }
            this.sleeping = false;
            return PlayState.STOP;
        }));
        animatableManager.add(new AnimationController<>(this, "idleController", 1, (event) -> {
            boolean shouldSleep = canSleep || (this.getVehicle() instanceof Starbuncle vehicle && vehicle.sleeping);
            if (!event.isMoving() && !shouldSleep) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
    }

    public void getNextItemFromPassengers() {
        if (getFirstPassenger() instanceof Starbuncle starbuncle && !starbuncle.getHeldStack().isEmpty()) {
            this.setHeldStack(starbuncle.getHeldStack().copyAndClear());
            starbuncle.getNextItemFromPassengers();
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return manager;
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        return SummonUtil.canSummonTakeDamage(pSource) && super.hurt(pSource, pAmount);
    }

    public boolean isTamed() {
        return this.entityData.get(TAMED);
    }

    public void setTamed(boolean tamed) {
        this.entityData.set(TAMED, tamed);
    }

    public String pathBlockDesc() {
        return this.entityData.get(PATH_BLOCK);
    }

    public void setPathBlockDesc(String name) {
        this.entityData.set(PATH_BLOCK, name);
    }

    public void attemptTame() {
        if (!isTamed() && this.getHeldStack().is(Tags.Items.NUGGETS_GOLD)) {
            tamingTime++;

            if (tamingTime > 60 && !level.isClientSide) {
                ItemStack stack = new ItemStack(ItemsRegistry.STARBUNCLE_SHARD.get(), 1 + level.random.nextInt(2));
                if (this.data.adopter != null && !this.data.adopter.isEmpty()) {
                    stack.setCount(1);
                    stack.set(DataComponentRegistry.STARBUNCLE_DATA, this.data.immutable());
                }
                level.addFreshEntity(new ItemEntity(level, getX(), getY() + 0.5, getZ(), stack));
                level.playSound(null, getX(), getY(), getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.NEUTRAL, 1f, 1f);
                ANCriteriaTriggers.rewardNearbyPlayers(ANCriteriaTriggers.POOF_MOB.get(), (ServerLevel) this.level, this.getOnPos(), 10);
                this.remove(RemovalReason.DISCARDED);
            } else if (tamingTime > 55 && level.isClientSide) {
                for (int i = 0; i < 10; i++) {
                    double d0 = getX();
                    double d1 = getY() + 0.1;
                    double d2 = getZ();
                    level.addParticle(ParticleTypes.END_ROD, d0, d1, d2, (level.random.nextFloat() * 1 - 0.5) / 3, (level.random.nextFloat() * 1 - 0.5) / 3, (level.random.nextFloat() * 1 - 0.5) / 3);
                }
            }
        }
    }

    @Override
    public void tick() {
        try {
            super.tick();
        } catch (NoClassDefFoundError error) {
            System.out.println("Starbuncle threaded pathing failed.");
            System.out.println(this);
            return;
        }
        if (this.dynamicBehavior != null) {
            this.dynamicBehavior.tick();
        }
        if (level.isClientSide && level.getGameTime() % 5 == 0) {
            this.canSleep = this.getBlockStateOn().is(BlockTagProvider.SUMMON_SLEEPABLE);
        }
        SummonUtil.healOverTime(this);
        if (!level.isClientSide && level.getGameTime() % 10 == 0 && this.getName().getString().toLowerCase(Locale.ROOT).equals("jeb_")) {
            this.entityData.set(COLOR, carbyColors[level.random.nextInt(carbyColors.length)]);
        }

        if (!level.isClientSide) {
            lastAABBCalc++;
            if (this.backOff > 0)
                this.backOff--;
            if (this.bedBackoff > 0) {
                this.bedBackoff--;
            }
        }
        if (!level.isClientSide && dynamicBehavior != null && level.getGameTime() % 100 == 0) {
            dynamicBehavior.syncTag();
        }
        if (this.dead)
            return;

        if (!level.isClientSide && this.getStarbuncleWithSpace() != null) {
            for (ItemEntity itementity : this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(1))) {
                if (itementity.isAlive() && !itementity.getItem().isEmpty() && !itementity.hasPickUpDelay()) {
                    this.pickUpItem(itementity);
                    if (getHeldStack() != null && !getHeldStack().isEmpty())
                        break;
                }
            }
        }
        attemptTame();
    }

    @Override
    public void onWanded(Player playerEntity) {
        boolean removeAcc = this.dynamicBehavior.clearOrRemove();
        this.dynamicBehavior.onWanded(playerEntity);
        data.pathBlock = null;
        data.bedPos = null;
        if (!getCosmeticItem().isEmpty() && removeAcc) {
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), getCosmeticItem().split(1)));
            if (!(dynamicBehavior instanceof StarbyTransportBehavior)) {
                dynamicBehavior = new StarbyTransportBehavior(this, new CompoundTag());
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.default_behavior"));
                syncBehavior();
            }
            this.setCosmeticItem(ItemStack.EMPTY);
        }
        PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.cleared"));
    }

    @Override
    public List<ColorPos> getWandHighlight(List<ColorPos> list) {
        return this.dynamicBehavior == null ? new ArrayList<>() : this.dynamicBehavior.getWandHighlight(list);
    }

    public void syncBehavior() {
        CompoundTag behaviorTag = dynamicBehavior.toTag(new CompoundTag());
        this.data.behaviorTag = behaviorTag;
        Networking.sendToNearbyClient(level, this, new PacketSyncTag(behaviorTag, getId()));
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @org.jetbrains.annotations.Nullable Direction side, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (this.isPassenger() && this.getRootVehicle() instanceof Starbuncle baseStarby) {
            baseStarby.dynamicBehavior.onFinishedConnectionFirst(storedPos, side, storedEntity, playerEntity);
            return;
        }
        dynamicBehavior.onFinishedConnectionFirst(storedPos, side, storedEntity, playerEntity);
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @org.jetbrains.annotations.Nullable Direction side, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (this.isPassenger() && this.getRootVehicle() instanceof Starbuncle baseStarby) {
            baseStarby.dynamicBehavior.onFinishedConnectionLast(storedPos, side, storedEntity, playerEntity);
            return;
        }
        dynamicBehavior.onFinishedConnectionLast(storedPos, side, storedEntity, playerEntity);
    }

    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2d).add(Attributes.STEP_HEIGHT, 1.1f);
    }


    @Override
    public boolean canCollideWith(@NotNull Entity pEntity) {
        if (pEntity instanceof Player)
            return false;
        return super.canCollideWith(pEntity);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    public void pickUpItem(@NotNull ItemEntity itemEntity) {
        if (!this.getHeldStack().isEmpty() && this.getStarbuncleWithSpace() == null)
            return;
        if (!this.isTamed() && itemEntity.getItem().is(Tags.Items.NUGGETS_GOLD)) {
            setHeldStack(itemEntity.getItem().split(1));
            return;
        }
        this.dynamicBehavior.pickUpItem(itemEntity);
    }

    @Override
    public void setRecordPlayingNearby(@NotNull BlockPos pos, boolean hasSound) {
        super.setRecordPlayingNearby(pos, hasSound);
        this.jukeboxPos = pos;
        this.partyCarby = hasSound;
    }

    // Cannot add conditional goals in RegisterGoals as it is final and called during the MobEntity super.
    protected void reloadGoals() {
        if (this.level.isClientSide())
            return;
        this.goalSelector.availableGoals.clear();
        for (WrappedGoal goal : getGoals()) {
            this.goalSelector.addGoal(goal.getPriority(), goal.getGoal());
        }
    }

    public List<WrappedGoal> getGoals() {
        List<WrappedGoal> list = new ArrayList<>();
        list.add(new WrappedGoal(0, new FloatGoal(this)));
        if (!this.isTamed()) {
            list.add(new WrappedGoal(1, new UntamedFindItem(this)));
            list.add(new WrappedGoal(4, new LookAtPlayerGoal(this, Player.class, 3.0F, 0.02F)));
            list.add(new WrappedGoal(4, new LookAtPlayerGoal(this, Mob.class, 8.0F)));
            list.add(new WrappedGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.2D)));
            list.add(new WrappedGoal(2, new AvoidEntityGoalMC<>(this, Player.class, 16.0F, 1.2D)));
            list.add(new WrappedGoal(0, new FloatGoal(this)));
        } else {
            list.addAll(dynamicBehavior.goals);
        }
        return list;
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.875f;
    }

    @Override
    public void die(@NotNull DamageSource source) {
        if (!level.isClientSide && isTamed()) {
            dropData();
        }
        super.die(source);
    }

    public void dropData() {
        ItemStack stack = new ItemStack(ItemsRegistry.STARBUNCLE_CHARM.get());
        stack.set(DataComponentRegistry.STARBUNCLE_DATA, data.immutable());
        level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
        if (this.getHeldStack() != null)
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), this.getHeldStack()));
    }

    public AABB getAABB() {
        if (cachedAAB == null || lastAABBCalc >= 60) {
            cachedAAB = new AABB(blockPosition()).inflate(8);
            lastAABBCalc = 0;
        }
        return cachedAAB;
    }

    @Override
    protected void updateControlFlags() {
        boolean flag = true;
        boolean flag1 = !(this.getVehicle() instanceof Boat);
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, flag);
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, flag && flag1);
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, flag);
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND || player.getCommandSenderWorld().isClientSide || !isTamed())
            return InteractionResult.SUCCESS;

        ItemStack stack = player.getItemInHand(hand);
        if (player.getMainHandItem().getItem() instanceof StarbuncleCharm) {
            Starbuncle toRide = this;
            while (toRide.getFirstPassenger() instanceof Starbuncle riding) {
                toRide = riding;
            }

            if (toRide.hasPassenger(e -> true)) {
                return InteractionResult.FAIL;
            }

            Starbuncle carbuncle = new Starbuncle(level, true);
            carbuncle.data = player.getMainHandItem().getOrDefault(DataComponentRegistry.STARBUNCLE_DATA, new StarbuncleCharmData()).mutable();
            carbuncle.setPos(toRide.position().add(0, toRide.getBoundingBox().getYsize(), 0));
            level.addFreshEntity(carbuncle);
            carbuncle.restoreFromTag();
            carbuncle.startRiding(toRide);
            if (!player.hasInfiniteMaterials()) {
                stack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }

        if (player.getMainHandItem().is(Tags.Items.DYES)) {
            DyeColor color = DyeColor.getColor(stack);
            if (color == null || this.entityData.get(COLOR).equals(color.getName()) || !Arrays.asList(carbyColors).contains(color.getName()))
                return InteractionResult.SUCCESS;
            setColor(color.getName());
            if (!player.hasInfiniteMaterials()) {
                player.getMainHandItem().shrink(1);
            }
            return InteractionResult.SUCCESS;
        }

        if (player.getMainHandItem().getItem() instanceof BlockItem blockItem) {
            data.pathBlock = blockItem.getBlock();
            setPathBlockDesc(Component.translatable(data.pathBlock.getDescriptionId()).getString());
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.starbuncle.path"));
        }

        return dynamicBehavior.mobInteract(player, hand);
    }

    @Override
    public @NotNull EntityType<?> getType() {
        return ModEntities.STARBUNCLE_TYPE.get();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(TAMED, false);
        pBuilder.define(COLOR, DyeColor.ORANGE.getName());
        pBuilder.define(PATH_BLOCK, "");
        pBuilder.define(HEAD_COSMETIC, ItemStack.EMPTY);
        pBuilder.define(BEHAVIOR_KEY, StarbyTransportBehavior.TRANSPORT_ID.toString());
        pBuilder.define(BEHAVIOR_TAG, new CompoundTag());
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    public void setHeldStack(ItemStack stack) {
        this.setItemSlot(EquipmentSlot.MAINHAND, stack == null ? ItemStack.EMPTY : stack);
    }

    public ItemStack getHeldStack() {
        return this.getMainHandItem();
    }

    /**
     * @return self if held item is empty, or the first starbuncle passenger
     */
    public Starbuncle getStarbuncleWithSpace() {
        if (this.getHeldStack().isEmpty()) {
            return this;
        }
        for (Entity e : this.getIndirectPassengers()) {
            if (!(e instanceof Starbuncle starbuncle))
                continue;
            if (starbuncle.getHeldStack().isEmpty()) {
                return starbuncle;
            }
        }
        return null;
    }

    public @NotNull ItemStack getCosmeticItem() {
        return this.entityData.get(HEAD_COSMETIC);
    }

    public void setCosmeticItem(ItemStack stack) {
        if (!this.entityData.get(HEAD_COSMETIC).isEmpty())
            this.level.addFreshEntity(new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), this.entityData.get(HEAD_COSMETIC)));
        this.entityData.set(HEAD_COSMETIC, stack);
        this.data.cosmetic = stack;
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if (this.isRemoved())
            return false;

        if (!level.isClientSide && isTamed()) {
            dropData();
            ParticleUtil.spawnPoof((ServerLevel) level, blockPosition());
            this.remove(RemovalReason.DISCARDED);
        }
        return this.isTamed();
    }

    private boolean setBehaviors;

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        data = NBTComponent.fromTag(StarbuncleCharmData.CODEC.codec(), tag.contains("starbuncleData") ? tag.getCompound("starbuncleData") : new CompoundTag()).mutable();
        this.dynamicBehavior = BehaviorRegistry.create(data.behaviorKey, this, data.behaviorTag);
        setHeldStack(ItemStack.parseOptional(level.registryAccess(), tag.getCompound("held")));

        backOff = tag.getInt("backoff");
        this.entityData.set(TAMED, tag.getBoolean("tamed"));
        if (!setBehaviors) {
            this.goalSelector.availableGoals = new LinkedHashSet<>();
            this.reloadGoals();
            setBehaviors = true;
            restoreFromTag();
            if (this.dynamicBehavior != null && !this.level.isClientSide) {
                this.dynamicBehavior.syncTag();
            }
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        data.behaviorTag = this.dynamicBehavior.toTag(new CompoundTag());
        tag.put("starbuncleData", data.immutable().toTag(level));
        if (!getHeldStack().isEmpty()) {
            Tag itemTag = getHeldStack().save(level.registryAccess());
            tag.put("held", itemTag);
        }
        tag.putInt("backoff", backOff);
        tag.putBoolean("tamed", this.entityData.get(TAMED));
    }

    public void restoreFromTag() {
        if (data.color != null)
            this.entityData.set(COLOR, data.color);
        if (data.pathBlock != null) {
            setPathBlockDesc(Component.translatable(data.pathBlock.getDescriptionId()).getString());
        }
        if (data.cosmetic != null && !data.cosmetic.isEmpty())
            this.entityData.set(HEAD_COSMETIC, data.cosmetic);
        setCustomName(data.name);
        if (data.behaviorKey != null) {
            this.entityData.set(BEHAVIOR_KEY, data.behaviorKey.toString());
        }
        if (data.behaviorTag != null) {
            this.dynamicBehavior = BehaviorRegistry.create(data.behaviorKey, this, data.behaviorTag);
            this.entityData.set(BEHAVIOR_TAG, dynamicBehavior.toTag(new CompoundTag()));
            this.reloadGoals();
        } else if (this.isTamed()) {
            this.dynamicBehavior = new StarbyTransportBehavior(this, new CompoundTag());
            this.entityData.set(BEHAVIOR_TAG, dynamicBehavior.toTag(new CompoundTag()));
            this.reloadGoals();
        }
        if (!level.isClientSide && this.dynamicBehavior != null) {
            this.dynamicBehavior.syncTag();
        }
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
        if (pKey == BEHAVIOR_TAG) {
            this.dynamicBehavior = BehaviorRegistry.create(data.behaviorKey, this, this.entityData.get(BEHAVIOR_TAG));
        }
    }

    @Override
    public void setCustomName(@Nullable Component pName) {
        super.setCustomName(pName);
        this.data.name = pName;
    }

    public int getBedBackoff() {
        return bedBackoff;
    }

    public void setBedBackoff(int bedBackoff) {
        this.bedBackoff = bedBackoff;
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (!isTamed())
            return;
        if (this.isPassenger() && this.getRootVehicle() instanceof Starbuncle baseStarby) {
            baseStarby.getTooltip(tooltip);
            return;
        }

        if (dynamicBehavior != null)
            dynamicBehavior.getTooltip(tooltip::add);
        if (pathBlockDesc() != null && !pathBlockDesc().isEmpty()) {
            tooltip.add(Component.translatable("ars_nouveau.starbuncle.pathing", this.entityData.get(PATH_BLOCK)));
        }
    }

    @Override
    public int getBaseExperienceReward() {
        return 0;
    }

    public void setColor(String color) {
        this.data.color = color;
        this.entityData.set(COLOR, data.color);
    }

    public String getColor() {
        return this.entityData.get(COLOR);
    }

    public static Map<String, ResourceLocation> TEXTURES = new HashMap<>(){
        {
            put("Gootastic", ArsNouveau.prefix("textures/entity/starbuncle_goo.png"));
            put("Sir Squirrely", ArsNouveau.prefix("textures/entity/sir_squirrely.png"));
            put("Zieg", ArsNouveau.prefix("textures/entity/zieg.png"));
            put("Xacris", ArsNouveau.prefix("textures/entity/xacris.png"));
            put("Xollus", ArsNouveau.prefix("textures/entity/starbuncle_jarva.png"));
            for(DyeColor color : DyeColor.values()) {
                put(color.getName(), ArsNouveau.prefix("textures/entity/starbuncle_" + color.getName().toLowerCase() + ".png"));
            }
        }
    };

    public static Map<String, ResourceLocation> MODELS = new HashMap<>(){
        {
            put("Gootastic", ArsNouveau.prefix("geo/goobuncle.geo.json"));
            put("Sir Squirrely", ArsNouveau.prefix("geo/sir_squirrely.geo.json"));
            put("Zieg", ArsNouveau.prefix("geo/zieg.geo.json"));
            put("Xacris", ArsNouveau.prefix("geo/xacris.geo.json"));
            put("starbuncle", ArsNouveau.prefix("geo/starbuncle.geo.json"));
            put("Xollus", ArsNouveau.prefix("geo/starbuncle_jarva.geo.json"));
        }
    };

    public ResourceLocation getTexture() {
        var nameTexture = TEXTURES.get(this.getName().getString());
        if(nameTexture != null){
            return nameTexture;
        }
        String color = getColor();
        if (color.isEmpty()) color = DyeColor.ORANGE.getName();
        return TEXTURES.get(color);
    }

    public ResourceLocation getModel(){
        String key = getName().getString();
        return MODELS.getOrDefault(key, MODELS.get("starbuncle"));
    }

    @SuppressWarnings("all")
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, @NotNull DifficultyInstance pDifficulty, @NotNull MobSpawnType pReason, @org.jetbrains.annotations.Nullable SpawnGroupData pSpawnData) {
        RandomSource randomSource = pLevel.getRandom();
        if (randomSource.nextFloat() <= 0.1f && !Rewards.starbuncles.isEmpty()) {
            try {
                Rewards.ContributorStarby contributorStarby = Rewards.starbuncles.get(randomSource.nextInt(Rewards.starbuncles.size()));
                this.setColor(contributorStarby.color);
                this.setCustomName(Component.literal(contributorStarby.name));
                this.data.bio = contributorStarby.bio;
                this.data.adopter = contributorStarby.adopter;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            this.setColor(carbyColors[randomSource.nextInt(carbyColors.length)]);
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
    }

    public static String[] carbyColors = Arrays.stream(DyeColor.values()).map(DyeColor::getName).toArray(String[]::new);

    public int getBackOff() {
        return backOff;
    }

    public void setBackOff(int backOff) {
        this.backOff = backOff;
    }

    @Override
    public IDebugger getDebugger() {
        return debugger;
    }

    public void addGoalDebug(Goal goal, DebugEvent debugEvent) {
        addGoalDebug((Object) goal, debugEvent);
    }

    public void addGoalDebug(Object goal, DebugEvent debugEvent) {
        addGoalDebug(goal, debugEvent, false);
    }

    public void addGoalDebug(Object goal, DebugEvent debugEvent, boolean storeDuplicate) {
        debugEvent.id = goal.getClass().getSimpleName() + "_" + debugEvent.id;
        addDebugEvent(debugEvent, storeDuplicate);
    }

    /*
     * Syncs the behavior tag to the client for rendering purposes.
     */
    @Override
    public void onTagSync(CompoundTag tag) {
        if (level.isClientSide) {
            this.dynamicBehavior = BehaviorRegistry.create(ResourceLocation.parse(entityData.get(BEHAVIOR_KEY)), this, tag);
        }
    }

}
