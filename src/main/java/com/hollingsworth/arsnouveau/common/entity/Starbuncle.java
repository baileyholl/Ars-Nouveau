package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.familiar.PersistentFamiliarData;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.SummonBed;
import com.hollingsworth.arsnouveau.common.compat.PatchouliHandler;
import com.hollingsworth.arsnouveau.common.entity.goal.AvoidEntityGoalMC;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyTransportBehavior;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.UntamedFindItem;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.MinecoloniesAdvancedPathNavigate;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.MovementHandler;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.PathingStuckHandler;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.*;

import static com.hollingsworth.arsnouveau.api.RegistryHelper.getRegistryName;

public class Starbuncle extends PathfinderMob implements IAnimatable, IDecoratable, IDispellable, ITooltipProvider, IWandable {

    public enum StarbuncleGoalState {
        FORAGING,
        HUNTING_ITEM,
        TAKING_ITEM,
        STORING_ITEM,
        RESTING,
        NONE
    }

    public StarbuncleGoalState goalState;
    private MinecoloniesAdvancedPathNavigate pathNavigate;

    public static final EntityDataAccessor<Boolean> TAMED = SynchedEntityData.defineId(Starbuncle.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> COLOR = SynchedEntityData.defineId(Starbuncle.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> PATH_BLOCK = SynchedEntityData.defineId(Starbuncle.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<ItemStack> HEAD_COSMETIC = SynchedEntityData.defineId(Starbuncle.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<CompoundTag> BEHAVIOR_TAG = SynchedEntityData.defineId(Starbuncle.class, EntityDataSerializers.COMPOUND_TAG);
    private int backOff; // Used to stop inventory store/take spam when chests are full or empty.
    public int tamingTime;
    private int lastAABBCalc;
    private AABB cachedAAB;

    public BlockPos jukeboxPos;
    public boolean partyCarby;
    public PathNavigation minecraftPathNav;
    public StarbuncleData data = new StarbuncleData(new CompoundTag());
    public ChangeableBehavior dynamicBehavior = new StarbyTransportBehavior(this, new CompoundTag());

    AnimationFactory manager = new AnimationFactory(this);

    public Starbuncle(EntityType<Starbuncle> entityCarbuncleEntityType, Level world) {
        super(entityCarbuncleEntityType, world);
        maxUpStep = 1.2f;
        dynamicBehavior = new StarbyTransportBehavior(this, new CompoundTag());
        addGoalsAfterConstructor();
        this.moveControl = new MovementHandler(this);
    }

    public Starbuncle(Level world, boolean tamed) {
        super(ModEntities.STARBUNCLE_TYPE.get(), world);
        this.setTamed(tamed);
        maxUpStep = 1.2f;
        this.moveControl = new MovementHandler(this);
        dynamicBehavior = new StarbyTransportBehavior(this, new CompoundTag());
        addGoalsAfterConstructor();
    }

    @Override
    public MinecoloniesAdvancedPathNavigate getNavigation() {
        if (this.pathNavigate == null) {
            this.pathNavigate = new MinecoloniesAdvancedPathNavigate(this, this.level);
            this.minecraftPathNav = this.navigation;
            this.navigation = pathNavigate;
            this.pathNavigate.setCanFloat(true);
            this.pathNavigate.setSwimSpeedFactor(2.0);
            this.pathNavigate.getPathingOptions().setEnterDoors(true);
            this.pathNavigate.getPathingOptions().setCanOpenDoors(true);
            this.pathNavigate.setStuckHandler(PathingStuckHandler.createStuckHandler().withTeleportOnFullStuck().withTeleportSteps(5));
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
        getEntityData().set(Starbuncle.BEHAVIOR_TAG, dynamicBehavior.toTag(new CompoundTag()));
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "walkController", 1, this::animationPredicate));
        animationData.addAnimationController(new AnimationController<>(this, "danceController", 1, this::dancePredicate));
        animationData.addAnimationController(new AnimationController<>(this, "sleepController", 1, this::sleepPredicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return manager;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source == DamageSource.CACTUS || source == DamageSource.SWEET_BERRY_BUSH || source == DamageSource.DROWN)
            return false;
        return super.hurt(source, amount);
    }

    private PlayState dancePredicate(AnimationEvent event) {
        if ((!this.isTamed() && getHeldStack().is(Tags.Items.NUGGETS_GOLD)) || (this.partyCarby && this.jukeboxPos != null && BlockUtil.distanceFrom(position, jukeboxPos) <= 8)) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("dance_master"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private PlayState animationPredicate(AnimationEvent event) {
        if (event.isMoving() || (level.isClientSide && PatchouliHandler.isPatchouliWorld())) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("run"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <T extends IAnimatable> PlayState sleepPredicate(AnimationEvent<T> event) {
        Block onBlock = level.getBlockState(new BlockPos(position)).getBlock();
        if (!event.isMoving() && (onBlock instanceof BedBlock || onBlock instanceof SummonBed)) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("resting"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
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
                level.addFreshEntity(new ItemEntity(level, getX(), getY() + 0.5, getZ(), stack));
                level.playSound(null, getX(), getY(), getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.NEUTRAL, 1f, 1f);
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
        if (!level.isClientSide && level.getGameTime() % 10 == 0 && this.getName().getString().toLowerCase(Locale.ROOT).equals("jeb_")) {
            this.entityData.set(COLOR, carbyColors[level.random.nextInt(carbyColors.length)]);
        }

        if (!level.isClientSide) {
            lastAABBCalc++;
            if(this.backOff > 0)
                this.backOff--;
        }
        if (this.dead)
            return;

        if (this.getHeldStack().isEmpty() && !level.isClientSide) {
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
        this.dynamicBehavior.onWanded(playerEntity);
        data.pathBlock = null;
        data.bedPos = null;
        PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.cleared"));
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        dynamicBehavior.onFinishedConnectionFirst(storedPos, storedEntity, playerEntity);
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        dynamicBehavior.onFinishedConnectionLast(storedPos, storedEntity, playerEntity);
    }

    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2d);
    }

    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        if(!this.getHeldStack().isEmpty())
            return;
        if(!this.isTamed() && itemEntity.getItem().is(Tags.Items.NUGGETS_GOLD)){
            setHeldStack(itemEntity.getItem().split(1));
            return;
        }
        this.dynamicBehavior.pickUpItem(itemEntity);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void setRecordPlayingNearby(BlockPos pos, boolean hasSound) {
        super.setRecordPlayingNearby(pos, hasSound);
        this.jukeboxPos = pos;
        this.partyCarby = hasSound;
    }

    // Cannot add conditional goals in RegisterGoals as it is final and called during the MobEntity super.
    protected void addGoalsAfterConstructor() {
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
        if(!this.isTamed()){
            list.add(new WrappedGoal(1, new UntamedFindItem(this)));
            list.add(new WrappedGoal(4, new LookAtPlayerGoal(this, Player.class, 3.0F, 0.02F)));
            list.add(new WrappedGoal(4, new LookAtPlayerGoal(this, Mob.class, 8.0F)));
            list.add(new WrappedGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.2D)));
            list.add(new WrappedGoal(2, new AvoidEntityGoalMC<>(this, Player.class, 16.0F, 2.0D, 1.2D)));
            list.add(new WrappedGoal(0, new FloatGoal(this)));
        }else{
            list.addAll(dynamicBehavior.goals);
        }
        return list;
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.875f;
    }

    @Override
    public void die(DamageSource source) {
        if (!level.isClientSide && isTamed()) {
            ItemStack stack = new ItemStack(ItemsRegistry.STARBUNCLE_CHARM.get());
            stack.setTag(data.toTag(this, new CompoundTag()));
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
            if (this.getHeldStack() != null)
                level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), this.getHeldStack()));
            if (!this.getCosmeticItem().isEmpty()) {
                level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), this.getCosmeticItem().copy()));
            }
        }
        super.die(source);
    }

    public AABB getAABB() {
        if (cachedAAB == null || lastAABBCalc >= 60) {
            cachedAAB = new AABB(blockPosition()).inflate(8);
            lastAABBCalc = 0;
        }
        return cachedAAB;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND || player.getCommandSenderWorld().isClientSide || !isTamed())
            return InteractionResult.SUCCESS;

        ItemStack stack = player.getItemInHand(hand);

        if (player.getMainHandItem().is(Tags.Items.DYES)) {
            DyeColor color = DyeColor.getColor(stack);
            if (color == null || this.entityData.get(COLOR).equals(color.getName()) || !Arrays.asList(carbyColors).contains(color.getName()))
                return InteractionResult.SUCCESS;
            setColor(color.getName());
            player.getMainHandItem().shrink(1);
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
    public EntityType<?> getType() {
        return ModEntities.STARBUNCLE_TYPE.get();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TAMED, false);
        this.entityData.define(COLOR, COLORS.ORANGE.name());
        this.entityData.define(PATH_BLOCK, "");
        this.entityData.define(HEAD_COSMETIC, ItemStack.EMPTY);
        this.entityData.define(BEHAVIOR_TAG, new CompoundTag());
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    public void setHeldStack(ItemStack stack) {
        this.setItemSlot(EquipmentSlot.MAINHAND, stack);
    }

    public ItemStack getHeldStack() {
        return this.getMainHandItem();
    }

    public ItemStack getCosmeticItem() {
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
            ItemStack charm = new ItemStack(ItemsRegistry.STARBUNCLE_CHARM.get());
            charm.setTag(data.toTag(this, new CompoundTag()));
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), charm.copy()));
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), getHeldStack()));
            if (!this.getCosmeticItem().isEmpty()) {
                level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), this.getCosmeticItem().copy()));
            }
            ParticleUtil.spawnPoof((ServerLevel) level, blockPosition());
            this.remove(RemovalReason.DISCARDED);
        }
        return this.isTamed();
    }

    private boolean setBehaviors;

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        data = new StarbuncleData(tag.contains("starbuncleData") ? tag.getCompound("starbuncleData") : new CompoundTag());
        if (tag.contains("held"))
            setHeldStack(ItemStack.of((CompoundTag) tag.get("held")));

        backOff = tag.getInt("backoff");
        this.entityData.set(TAMED, tag.getBoolean("tamed"));
        if (!setBehaviors) {
            this.goalSelector.availableGoals = new LinkedHashSet<>();
            this.addGoalsAfterConstructor();
            setBehaviors = true;
            restoreFromTag();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("starbuncleData", data.toTag(this, new CompoundTag()));
        if (getHeldStack() != null) {
            CompoundTag itemTag = new CompoundTag();
            getHeldStack().save(itemTag);
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
        if(data.behaviorTag != null){
            this.dynamicBehavior = BehaviorRegistry.create(this, data.behaviorTag);
            this.entityData.set(BEHAVIOR_TAG, dynamicBehavior.toTag(new CompoundTag()));
            this.addGoalsAfterConstructor();
        }else if(this.isTamed()){
            this.dynamicBehavior = new StarbyTransportBehavior(this, new CompoundTag());
            this.entityData.set(BEHAVIOR_TAG, dynamicBehavior.toTag(new CompoundTag()));
            this.addGoalsAfterConstructor();
        }
    }

    @Override
    public void setCustomName(@Nullable Component pName) {
        super.setCustomName(pName);
        this.data.name = pName;
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        dynamicBehavior.getTooltip(tooltip);
        if (pathBlockDesc() != null && !pathBlockDesc().isEmpty()) {
            tooltip.add(Component.translatable("ars_nouveau.starbuncle.pathing", this.entityData.get(PATH_BLOCK)));
        }
    }

    @Override
    public int getExperienceReward() {
        return 0;
    }

    @Override
    public boolean canCollideWith(Entity p_241849_1_) {
        return true;
    }

    public void setColor(String color) {
        this.data.color = color;
        this.entityData.set(COLOR, data.color);
    }

    public String getColor() {
        return this.entityData.get(COLOR);
    }

    public static String[] carbyColors = {"purple", "orange", "blue", "red", "yellow", "green"};

    public int getBackOff() {
        return backOff;
    }

    public void setBackOff(int backOff) {
        this.backOff = backOff;
    }

    public enum COLORS {
        ORANGE,
        PURPLE,
        GREEN,
        BLUE,
        RED,
        YELLOW
    }

    public static class StarbuncleData extends PersistentFamiliarData<Starbuncle> {
        public Block pathBlock;
        public BlockPos bedPos;
        public CompoundTag behaviorTag;

        public StarbuncleData(CompoundTag tag) {
            super(tag);

            if (tag.contains("path")) {
                pathBlock = Registry.BLOCK.get(new ResourceLocation(tag.getString("path")));
            }
            bedPos = NBTUtil.getBlockPos(tag, "bed_");
            if (bedPos.equals(BlockPos.ZERO))
                bedPos = null;
            if(tag.contains("behavior"))
                behaviorTag = tag.getCompound("behavior");
        }

        @Override
        public CompoundTag toTag(Starbuncle starbuncle, CompoundTag tag) {
            super.toTag(starbuncle, tag);
            if (pathBlock != null)
                tag.putString("path", getRegistryName(pathBlock).toString());
            if (bedPos != null)
                NBTUtil.storeBlockPos(tag, "bed_", bedPos);
            tag.put("behavior", starbuncle.dynamicBehavior.toTag(new CompoundTag()));
            return tag;
        }
    }
}
