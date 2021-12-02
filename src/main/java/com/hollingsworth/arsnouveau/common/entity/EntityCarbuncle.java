package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.compat.PatchouliHandler;
import com.hollingsworth.arsnouveau.common.entity.goal.AvoidEntityGoalMC;
import com.hollingsworth.arsnouveau.common.entity.goal.GetUnstuckGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.*;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.MinecoloniesAdvancedPathNavigate;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.MovementHandler;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.PathingStuckHandler;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.GrassPathBlock;
import net.minecraft.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.*;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;

public class EntityCarbuncle extends PathfinderMob implements IAnimatable, IDispellable, ITooltipProvider, IWandable {

    public List<ItemStack> allowedItems = new ArrayList<>(); // Items the carbuncle is allowed to take
    public List<ItemStack> ignoreItems = new ArrayList<>(); // Items the carbuncle will not take
    public Block pathBlock;
    public boolean whitelist;
    public boolean blacklist;
    public List<BlockPos> TO_LIST = new ArrayList<>();
    public List<BlockPos> FROM_LIST = new ArrayList<>();

    private MinecoloniesAdvancedPathNavigate pathNavigate;

    public static final EntityDataAccessor<Integer> TO_POS = SynchedEntityData.defineId(EntityCarbuncle.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> FROM_POS = SynchedEntityData.defineId(EntityCarbuncle.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<ItemStack> HELD_ITEM = SynchedEntityData.defineId(EntityCarbuncle.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<Boolean> TAMED = SynchedEntityData.defineId(EntityCarbuncle.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> COLOR = SynchedEntityData.defineId(EntityCarbuncle.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> PATH_BLOCK = SynchedEntityData.defineId(EntityCarbuncle.class, EntityDataSerializers.STRING);

    private int backOff; // Used to stop inventory store/take spam when chests are full or empty.
    public int tamingTime;
    public boolean isStuck;
    private int lastAABBCalc;
    private AABB cachedAAB;

    public BlockPos jukeboxPos;
    public boolean partyCarby;

    AnimationFactory manager = new AnimationFactory(this);

    public EntityCarbuncle(EntityType<EntityCarbuncle> entityCarbuncleEntityType, Level world) {
        super(entityCarbuncleEntityType, world);
        maxUpStep = 1.2f;
        addGoalsAfterConstructor();
        this.moveControl = new MovementHandler(this);
    }

    public EntityCarbuncle(Level world, boolean tamed) {
        super(ModEntities.ENTITY_CARBUNCLE_TYPE, world);
        this.setTamed(tamed);
        maxUpStep = 1.2f;
        this.moveControl = new MovementHandler(this);
        addGoalsAfterConstructor();
    }

    @Override
    public MinecoloniesAdvancedPathNavigate getNavigation()
    {
        if (this.pathNavigate == null)
        {
            this.pathNavigate = new MinecoloniesAdvancedPathNavigate(this, this.level);
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

    public Boolean isOnRoad(BlockState state){
        return state.getBlock() instanceof GrassPathBlock || (pathBlock != null && pathBlock == state.getBlock());
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "walkController", 1, this::animationPredicate));
        animationData.addAnimationController(new AnimationController<>(this, "danceController", 1, this::dancePredicate));
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
        if (this.partyCarby && this.jukeboxPos != null && BlockUtil.distanceFrom(position, jukeboxPos) <= 8) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("dance_master2"));
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
        if (!isTamed() && this.getHeldStack().getItem() == Items.GOLD_NUGGET) {
            tamingTime++;
            if (tamingTime % 20 == 0 && !level.isClientSide())
                Networking.sendToNearby(level, this, new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, blockPosition()));

            if (tamingTime > 60 && !level.isClientSide) {
                ItemStack stack = new ItemStack(ItemsRegistry.carbuncleShard, 1 + level.random.nextInt(2));
                level.addFreshEntity(new ItemEntity(level, getX(), getY() + 0.5, getZ(), stack));
                this.remove(false);
                level.playSound(null, getX(), getY(), getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.NEUTRAL, 1f, 1f);
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
        super.tick();
        if(!level.isClientSide && level.getGameTime() % 10 == 0 && this.getName().getString().toLowerCase(Locale.ROOT).equals("jeb_")){
            this.entityData.set(COLOR, carbyColors[level.random.nextInt(carbyColors.length)]);
        }

        if (!level.isClientSide) {
            lastAABBCalc++;
        }

        if (this.backOff > 0 && !level.isClientSide)
            this.backOff--;
        if (this.dead)
            return;

        if (this.getHeldStack().isEmpty() && !level.isClientSide) {

                // Cannot use a single expanded bounding box because we don't want this to overlap with an adjacentt inventory that also has a frame.
            for (ItemEntity itementity : this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(1))) {
                if (itementity.isAlive() && !itementity.getItem().isEmpty() && !itementity.hasPickUpDelay()) {
                    if (!isTamed() && itementity.getItem().getItem() != Items.GOLD_NUGGET)
                        return;
                    this.pickUpItem(itementity);
                    if(getHeldStack() != null && !getHeldStack().isEmpty())
                        break;
                }
            }


        }
        attemptTame();
    }


    @Override
    public void onWanded(Player playerEntity) {
        this.whitelist = false;
        this.blacklist = false;
        this.FROM_LIST = new ArrayList<>();
        this.TO_LIST = new ArrayList<>();
        this.entityData.set(TO_POS, 0);
        this.entityData.set(FROM_POS, 0);
        PortUtil.sendMessage(playerEntity, new TranslatableComponent("ars_nouveau.carbuncle.cleared"));
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos == null)
            return;
        if (level.getBlockEntity(storedPos) != null && level.getBlockEntity(storedPos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()) {
            PortUtil.sendMessage(playerEntity, new TranslatableComponent("ars_nouveau.carbuncle.store"));
            setToPos(storedPos);
        }
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos == null)
            return;

        if (level.getBlockEntity(storedPos) != null && level.getBlockEntity(storedPos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()) {
            PortUtil.sendMessage(playerEntity, new TranslatableComponent("ars_nouveau.carbuncle.take"));
            setFromPos(storedPos);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 45) {
            ItemStack itemstack = this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (!itemstack.isEmpty()) {
                for (int i = 0; i < 8; ++i) {
                    Vec3 vec3d = (new Vec3(((double) this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D)).xRot(-this.xRot * ((float) Math.PI / 180F)).yRot(-this.yRot * ((float) Math.PI / 180F));
                    this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, itemstack), this.getX() + this.getLookAngle().x / 2.0D, this.getY(), this.getZ() + this.getLookAngle().z / 2.0D, vec3d.x, vec3d.y + 0.05D, vec3d.z);
                }
            }
        } else {
            super.handleEntityEvent(id);
        }

    }

    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2d);
    }

    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        if (this.getHeldStack().isEmpty() && isValidItem(itemEntity.getItem())) {
            setHeldStack(itemEntity.getItem());
            itemEntity.remove();
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_PICKUP, this.getSoundSource(), 1.0F, 1.0F);
            if(!isTamed())
                return;
            for(ItemEntity i : level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(3))){
                if(itemEntity.getItem().getCount() >= itemEntity.getItem().getMaxStackSize())
                    break;
                int maxTake = getHeldStack().getMaxStackSize() - getHeldStack().getCount();
                if(i.getItem().sameItem(getHeldStack())){
                    int toTake = Math.min(i.getItem().getCount(), maxTake);
                    i.getItem().shrink(toTake);
                    getHeldStack().grow(toTake);
                }
            }
        }
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

        for (WrappedGoal goal : getGoals()) {
            this.goalSelector.addGoal(goal.getPriority(), goal.getGoal());
        }
    }

    public List<WrappedGoal> getGoals() {
        return Boolean.TRUE.equals(this.entityData.get(TAMED)) ? getTamedGoals() : getUntamedGoals();
    }


    public BlockPos getHome() {
        if (FROM_LIST.isEmpty() && !TO_LIST.isEmpty())
            return TO_LIST.get(0);
        if (TO_LIST.isEmpty() && !FROM_LIST.isEmpty())
            return FROM_LIST.get(0);
        if (!TO_LIST.isEmpty())
            return FROM_LIST.get(0);
        return null;
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.875f;
    }

    //MOJANG MAKES THIS SO CURSED WHAT THE HECK
    public List<WrappedGoal> getTamedGoals() {
        List<WrappedGoal> list = new ArrayList<>();
        list.add(new WrappedGoal(1, new GetUnstuckGoal(this, () -> this.isStuck, stuck -> {
            this.isStuck = stuck;
            return null;
        })));
        list.add(new WrappedGoal(1, new FindItem(this)));
        list.add(new WrappedGoal(2, new ForageManaBerries(this)));
        list.add(new WrappedGoal(3, new StoreItemGoal(this)));
        list.add(new WrappedGoal(3, new TakeItemGoal(this)));
        list.add(new WrappedGoal(8, new LookAtPlayerGoal(this, Player.class, 3.0F, 0.01F)));
        list.add(new WrappedGoal(8, new NonHoggingLook(this, Mob.class, 3.0F, 0.01f)));
        list.add(new WrappedGoal(0, new FloatGoal(this)));
        list.add(new WrappedGoal(1, new OpenDoorGoal(this, true)));
//        list.add(new PrioritizedGoal(1, new OpenTrapdoorGoal(this, true)));
        return list;
    }

    public List<WrappedGoal> getUntamedGoals() {
        List<WrappedGoal> list = new ArrayList<>();
        list.add(new WrappedGoal(1, new FindItem(this)));
        list.add(new WrappedGoal(4, new LookAtPlayerGoal(this, Player.class, 3.0F, 0.02F)));
        list.add(new WrappedGoal(4, new LookAtPlayerGoal(this, Mob.class, 8.0F)));
        list.add(new WrappedGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.2D)));
        list.add(new WrappedGoal(2, new AvoidEntityGoalMC<>(this, Player.class, 16.0F, 2.0D, 1.2D)));
        list.add(new WrappedGoal(0, new FloatGoal(this)));
        return list;
    }

    @Override
    public void die(DamageSource source) {
        if (!level.isClientSide && isTamed()) {
            ItemStack stack = new ItemStack(ItemsRegistry.carbuncleCharm);
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
            if (this.getHeldStack() != null)
                level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), this.getHeldStack()));
        }
        super.die(source);
    }

    public AABB getAABB(){
        if(cachedAAB == null || lastAABBCalc >= 60) {
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

        if (player.getMainHandItem().getItem().is(Tags.Items.DYES)) {
            DyeColor color = DyeColor.getColor(stack);
            if(color == null || this.entityData.get(COLOR).equals(color.getName()) || !Arrays.asList(carbyColors).contains(color.getName()))
                return InteractionResult.SUCCESS;
            this.entityData.set(COLOR, color.getName());
            player.getMainHandItem().shrink(1);
            return InteractionResult.SUCCESS;
        }

        if(player.getMainHandItem().getItem() instanceof BlockItem){
            pathBlock = ((BlockItem) player.getMainHandItem().getItem()).getBlock();
            setPathBlockDesc(new TranslatableComponent(pathBlock.getDescriptionId()).getString());
            PortUtil.sendMessage(player, new TranslatableComponent("ars_nouveau.carbuncle.path"));
        }

        if (player.getMainHandItem().isEmpty() && this.isTamed()) {
            StringBuilder status = new StringBuilder();
            if (whitelist && allowedItems != null) {
                status.append(new TranslatableComponent("ars_nouveau.carbuncle.whitelist").getString());
                for (ItemStack i : allowedItems) {
                    status.append(i.getHoverName().getString());
                }
            } else if (blacklist && allowedItems != null) {
                status.append(new TranslatableComponent("ars_nouveau.carbuncle.blacklist").getString());
                for (ItemStack i : ignoreItems) {
                    status.append(i.getHoverName().getString());
                }
            }
            if (!status.toString().isEmpty())
                PortUtil.sendMessage(player, status.toString());
        }

        if (!(stack.getItem() instanceof ItemScroll) || !stack.hasTag())
            return InteractionResult.FAIL;
        if (stack.getItem() == ItemsRegistry.ALLOW_ITEM_SCROLL) {
            List<ItemStack> items = ItemsRegistry.ALLOW_ITEM_SCROLL.getItems(stack);
            if (!items.isEmpty()) {
                this.allowedItems = ItemsRegistry.ALLOW_ITEM_SCROLL.getItems(stack);
                whitelist = true;
                blacklist = false;
                PortUtil.sendMessage(player, new TranslatableComponent("ars_nouveau.allow_set"));
            }
            return InteractionResult.SUCCESS;
        }

        if (stack.getItem() == ItemsRegistry.DENY_ITEM_SCROLL) {
            List<ItemStack> items = ItemsRegistry.DENY_ITEM_SCROLL.getItems(stack);
            if (!items.isEmpty()) {
                this.ignoreItems = ItemsRegistry.DENY_ITEM_SCROLL.getItems(stack);
                whitelist = false;
                blacklist = true;
                PortUtil.sendMessage(player, new TranslatableComponent("ars_nouveau.ignore_set"));
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_CARBUNCLE_TYPE;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HELD_ITEM, ItemStack.EMPTY);
        this.entityData.define(TAMED, false);
        this.entityData.define(TO_POS, 0);
        this.entityData.define(FROM_POS, 0);
        this.entityData.define(COLOR, COLORS.ORANGE.name());
        this.entityData.define(PATH_BLOCK, "");
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

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if (this.removed)
            return false;

        if (!level.isClientSide && isTamed()) {
            ItemStack stack = new ItemStack(ItemsRegistry.carbuncleCharm);
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack.copy()));
            stack = getHeldStack();
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
            ParticleUtil.spawnPoof((ServerLevel) level, blockPosition());
            this.remove();
        }
        return this.isTamed();
    }

    private boolean setBehaviors;

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("held"))
            setHeldStack(ItemStack.of((CompoundTag) tag.get("held")));
        FROM_LIST = new ArrayList<>();
        TO_LIST = new ArrayList<>();
        int counter = 0;

        while(NBTUtil.hasBlockPos(tag, "from_" + counter)){
            BlockPos pos = NBTUtil.getBlockPos(tag, "from_" + counter);
            if(!this.FROM_LIST.contains(pos))
                this.FROM_LIST.add(pos);
            counter++;
        }

        counter = 0;
        while(NBTUtil.hasBlockPos(tag, "to_" + counter)){
            BlockPos pos = NBTUtil.getBlockPos(tag, "to_" + counter);
            if(!this.TO_LIST.contains(pos))
                this.TO_LIST.add(pos);
            counter++;
        }

        BlockPos oldToPos = NBTUtil.getBlockPos(tag, "to");
        if(!oldToPos.equals(new BlockPos(0, 0, 0)) && !TO_LIST.contains(oldToPos))
            TO_LIST.add(oldToPos);
        //setToPos(NBTUtil.getBlockPos(tag, "to"));
        BlockPos oldFromPos = NBTUtil.getBlockPos(tag, "from");
        if(!oldFromPos.equals(new BlockPos(0, 0, 0)) && !FROM_LIST.contains(oldFromPos))
            FROM_LIST.add(oldFromPos);

        backOff = tag.getInt("backoff");
        tamingTime = tag.getInt("taming_time");
        whitelist = tag.getBoolean("whitelist");
        blacklist = tag.getBoolean("blacklist");

        // Remove goals and read them AFTER our tamed param is set because we can't ACCESS THEM OTHERWISE
        if (!setBehaviors)
            this.removeGoals();
        this.entityData.set(TAMED, tag.getBoolean("tamed"));
        if (!setBehaviors) {
            this.addGoalsAfterConstructor();
            setBehaviors = true;
        }
        allowedItems = NBTUtil.readItems(tag, "allowed_");
        ignoreItems = NBTUtil.readItems(tag, "ignored_");
        isStuck = tag.getBoolean("stuck");

        if (tag.contains("color"))
            this.entityData.set(COLOR, tag.getString("color"));

        this.entityData.set(TO_POS, TO_LIST.size());
       this.entityData.set(FROM_POS, FROM_LIST.size());
       if(tag.contains("path")){
           pathBlock = Registry.BLOCK.get(new ResourceLocation(tag.getString("path")));
           setPathBlockDesc(new TranslatableComponent(pathBlock.getDescriptionId()).getString());
       }
    }


    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (getHeldStack() != null) {
            CompoundTag itemTag = new CompoundTag();
            getHeldStack().save(itemTag);
            tag.put("held", itemTag);
        }

        int counter = 0;
        for(BlockPos p : FROM_LIST){
            NBTUtil.storeBlockPos(tag, "from_" +counter, p);
            counter++;
        }
        counter = 0;
        for(BlockPos p : TO_LIST){
            NBTUtil.storeBlockPos(tag, "to_" +counter, p);
            counter ++;
        }
        tag.putInt("backoff", backOff);
        tag.putBoolean("tamed", this.entityData.get(TAMED));
        tag.putInt("taming_time", tamingTime);
        tag.putBoolean("whitelist", whitelist);
        tag.putBoolean("blacklist", blacklist);
        if (allowedItems != null && !allowedItems.isEmpty())
            NBTUtil.writeItems(tag, "allowed_", allowedItems);

        if (ignoreItems != null && !ignoreItems.isEmpty())
            NBTUtil.writeItems(tag, "ignored_", ignoreItems);
        tag.putBoolean("stuck", isStuck);
        tag.putString("color", this.entityData.get(COLOR));
        if(pathBlock != null)
            tag.putString("path", pathBlock.getRegistryName().toString());
    }

    public void removeGoals() {
        this.goalSelector.availableGoals = new LinkedHashSet<>();
    }

    @Override
    public List<String> getTooltip() {
        List<String> toolTip = new ArrayList<>();
        if(!isTamed())
            return toolTip;
        toolTip.add(new TranslatableComponent("ars_nouveau.carbuncle.storing", this.entityData.get(TO_POS)).getString());
        toolTip.add(new TranslatableComponent("ars_nouveau.carbuncle.taking", this.entityData.get(FROM_POS)).getString());
        if(pathBlockDesc() != null && !pathBlockDesc().isEmpty()){
            toolTip.add(new TranslatableComponent("ars_nouveau.carbuncle.pathing", this.entityData.get(PATH_BLOCK)).getString());
        }
        return toolTip;
    }


    private ItemScroll.SortPref canDepositItem(BlockEntity tile, ItemStack stack) {
        ItemScroll.SortPref pref = ItemScroll.SortPref.LOW;
        if (tile == null || stack == null || stack.isEmpty())
            return ItemScroll.SortPref.INVALID;

        IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if(handler == null)
            return ItemScroll.SortPref.INVALID;
        for (ItemFrame i : level.getEntitiesOfClass(ItemFrame.class, new AABB(tile.getBlockPos()).inflate(1))) {
            // Check if these frames are attached to the tile
            BlockEntity adjTile = level.getBlockEntity(i.blockPosition().relative(i.getDirection().getOpposite()));
            if(adjTile == null || !adjTile.equals(tile))
                continue;

            if (i.getItem().isEmpty())
                continue;

            ItemStack stackInFrame = i.getItem();

            if(stackInFrame.getItem() instanceof ItemScroll){
                pref = ((ItemScroll) stackInFrame.getItem()).getSortPref(stack, stackInFrame.getOrCreateTag(), handler);
                // If our item frame just contains a normal item
            }else if (i.getItem().getItem() != stack.getItem()) {
                return ItemScroll.SortPref.INVALID;
            }else if(i.getItem().getItem() == stack.getItem()) {
                pref = ItemScroll.SortPref.HIGHEST;
            }
        }
        return !ItemStack.matches(ItemHandlerHelper.insertItemStacked(handler, stack.copy(), true), stack) ? pref : ItemScroll.SortPref.INVALID;
    }

    @Override
    protected int getExperienceReward(Player player) {
        return 0;
    }

    public BlockPos getValidStorePos(ItemStack stack){
        BlockPos returnPos = null;
        if(TO_LIST == null)
            return returnPos;
        ItemScroll.SortPref foundPref = ItemScroll.SortPref.INVALID;
        for(BlockPos b : TO_LIST){
            ItemScroll.SortPref pref = canDepositItem(level.getBlockEntity(b), stack);
            // Pick our highest priority
            if(pref.ordinal() > foundPref.ordinal()){
                foundPref = pref;
                returnPos = b;
            }
        }
        return returnPos;
    }

    public BlockPos getValidTakePos(){
        if(FROM_LIST == null)
            return null;

        for(BlockPos p : FROM_LIST){
            if(level.getBlockEntity(p) == null)
                continue;

            IItemHandler iItemHandler = level.getBlockEntity(p).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
            if(iItemHandler == null)
                continue;
            for(int j = 0; j < iItemHandler.getSlots(); j++){
                if(!iItemHandler.getStackInSlot(j).isEmpty() && isValidItem( iItemHandler.getStackInSlot(j)) && getValidStorePos(iItemHandler.getStackInSlot(j)) != null){
                    return p;
                }
            }
        }
        return null;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity p_241849_1_) {
        return true;
    }

    /**
     * Returns the maximum stack size an inventory can accept for a particular stack. Does all needed validity checks.
     */
    public int getMaxTake(ItemStack stack){
        if(!isValidItem(stack)){
            return -1;
        }
        BlockPos validStorePos = getValidStorePos(stack);
        if(validStorePos == null)
            return -1;
        IItemHandler handler = level.getBlockEntity(validStorePos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if(handler == null)
            return -1;

        for(int i = 0; i < handler.getSlots(); i++){
            ItemStack handlerStack = handler.getStackInSlot(i);
            if(ItemHandlerHelper.canItemStacksStack(handler.getStackInSlot(i), stack) || handlerStack.isEmpty()){
                if(handlerStack.isEmpty())
                    return handler.getSlotLimit(i);

                int maxRoom = handlerStack.getMaxStackSize() - handlerStack.getCount();
                if(maxRoom > 0)
                    return Math.min(maxRoom, handler.getSlotLimit(i));
            }
        }
        return -1;
    }

    public boolean isValidItem(ItemStack stack){
        if(stack.isEmpty())
            return false;
        if(!isTamed() && stack.getItem() == Items.GOLD_NUGGET)
            return true;

        if(getValidStorePos(stack) == null) {
            return false;
        }

        if(!whitelist && !blacklist)
            return true;
        if(whitelist){
            for(ItemStack s : allowedItems) {
                if (s.sameItem(stack)) {
                    return true;
                }
            }
            return false;
        }
        for(ItemStack s : ignoreItems)
            if(s.sameItem(stack))
                return false;
        return true;
    }

    public void setFromPos(BlockPos fromPos) {
        if(!this.FROM_LIST.contains(fromPos))
            this.FROM_LIST.add(fromPos.immutable());
        this.entityData.set(FROM_POS, FROM_LIST.size());
    }

    public void setToPos(BlockPos toPos) {
        if(!this.TO_LIST.contains(toPos))
            this.TO_LIST.add(toPos.immutable());
        this.entityData.set(TO_POS, TO_LIST.size());
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
        GREEN
    }

}
