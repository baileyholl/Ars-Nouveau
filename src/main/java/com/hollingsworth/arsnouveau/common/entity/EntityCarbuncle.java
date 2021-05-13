package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.goal.GetUnstuckGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.*;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;


public class EntityCarbuncle extends CreatureEntity implements IAnimatable, IDispellable, ITooltipProvider, IWandable {


    private BlockPos fromPos;
    private BlockPos toPos;
    public List<ItemStack> allowedItems; // Items the carbuncle is allowed to take
    public List<ItemStack> ignoreItems; // Items the carbuncle will not take
    public boolean whitelist;
    public boolean blacklist;
    public List<BlockPos> TO_LIST = new ArrayList<>();
    public List<BlockPos> FROM_LIST = new ArrayList<>();
    public static final DataParameter<Integer> TO_POS = EntityDataManager.defineId(EntityCarbuncle.class, DataSerializers.INT);
    public static final DataParameter<Integer> FROM_POS = EntityDataManager.defineId(EntityCarbuncle.class, DataSerializers.INT);

    public static final DataParameter<ItemStack> HELD_ITEM = EntityDataManager.defineId(EntityCarbuncle.class, DataSerializers.ITEM_STACK);
    public static final DataParameter<Boolean> TAMED = EntityDataManager.defineId(EntityCarbuncle.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> HOP = EntityDataManager.defineId(EntityCarbuncle.class, DataSerializers.BOOLEAN);
    public static final DataParameter<String> COLOR = EntityDataManager.defineId(EntityCarbuncle.class, DataSerializers.STRING);
    public int backOff; // Used to stop inventory store/take spam when chests are full or empty.
    public int tamingTime;
    public boolean isStuck;
    private int lastAABBCalc;
    private AxisAlignedBB cachedAAB;
    AnimationFactory manager = new AnimationFactory(this);

    public EntityCarbuncle(EntityType<EntityCarbuncle> entityCarbuncleEntityType, World world) {
        super(entityCarbuncleEntityType, world);
        maxUpStep = 1.2f;
        addGoalsAfterConstructor();
    }

    public EntityCarbuncle(World world, boolean tamed) {
        super(ModEntities.ENTITY_CARBUNCLE_TYPE, world);
        this.setTamed(tamed);
        maxUpStep = 1.2f;
        addGoalsAfterConstructor();
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<EntityCarbuncle>(this, "walkController", 20, this::animationPredicate));
        animationData.addAnimationController(new AnimationController<EntityCarbuncle>(this, "idleController", 20, this::idlePredicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return manager;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source == DamageSource.CACTUS || source == DamageSource.SWEET_BERRY_BUSH)
            return false;
        return super.hurt(source, amount);
    }


    private PlayState idlePredicate(AnimationEvent event) {
        if (level.getGameTime() % 20 == 0 && level.random.nextInt(3) == 0 && !this.entityData.get(HOP)) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("idle"));
        }

        return PlayState.CONTINUE;
    }

    private PlayState animationPredicate(AnimationEvent event) {
        if (this.entityData.get(HOP)) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("hop"));
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


    public void attemptTame() {
        if (!isTamed() && this.getHeldStack().getItem() == Items.GOLD_NUGGET) {
            tamingTime++;
            if (tamingTime % 20 == 0 && !level.isClientSide())
                Networking.sendToNearby(level, this, new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, blockPosition()));

            if (tamingTime > 60 && !level.isClientSide) {
                ItemStack stack = new ItemStack(ItemsRegistry.carbuncleShard, 1 + level.random.nextInt(2));
                level.addFreshEntity(new ItemEntity(level, getX(), getY() + 0.5, getZ(), stack));
                this.remove(false);
                level.playSound(null, getX(), getY(), getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundCategory.NEUTRAL, 1f, 1f);
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

        if (!level.isClientSide) {
            lastAABBCalc++;
            if (this.navigation.isDone()) {
                EntityCarbuncle.this.entityData.set(HOP, false);
            } else {
                EntityCarbuncle.this.entityData.set(HOP, true);
            }
        }

        if (this.backOff > 0 && !level.isClientSide)
            this.backOff--;
        if (this.dead)
            return;
        Direction[] directions = Direction.values();
        if (this.getHeldStack().isEmpty() && !level.isClientSide) {

                // Cannot use a single expanded bounding box because we don't want this to overlap with an adjacentt inventory that also has a frame.
            for (ItemEntity itementity : this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(1))) {
                if (!itementity.removed && !itementity.getItem().isEmpty() && !itementity.hasPickUpDelay()) {
                    if (!isTamed() && itementity.getItem().getItem() != Items.GOLD_NUGGET)
                        return;
                    this.pickUpItem(itementity);
                    this.entityData.set(HOP, false);
                    if(getHeldStack() != null && !getHeldStack().isEmpty())
                        break;
                }
            }


        }
        attemptTame();
    }


    @Override
    public void onWanded(PlayerEntity playerEntity) {
        this.whitelist = false;
        this.blacklist = false;
        this.FROM_LIST = new ArrayList<>();
        this.TO_LIST = new ArrayList<>();
        this.entityData.set(TO_POS, 0);
        this.entityData.set(FROM_POS, 0);
        PortUtil.sendMessage(playerEntity, new TranslationTextComponent("ars_nouveau.carbuncle.cleared"));
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, PlayerEntity playerEntity) {
        if (storedPos == null)
            return;
        if (level.getBlockEntity(storedPos) != null && level.getBlockEntity(storedPos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()) {
            PortUtil.sendMessage(playerEntity, new TranslationTextComponent("ars_nouveau.carbuncle.store"));
            setToPos(storedPos);
        }
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, PlayerEntity playerEntity) {
        if (storedPos == null)
            return;

        if (level.getBlockEntity(storedPos) != null && level.getBlockEntity(storedPos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()) {
            PortUtil.sendMessage(playerEntity, new TranslationTextComponent("ars_nouveau.carbuncle.take"));
            setFromPos(storedPos);
        }
    }

    /**
     * Handler for {@link World#setEntityState}
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 45) {
            ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.MAINHAND);
            if (!itemstack.isEmpty()) {
                for (int i = 0; i < 8; ++i) {
                    Vector3d vec3d = (new Vector3d(((double) this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D)).xRot(-this.xRot * ((float) Math.PI / 180F)).yRot(-this.yRot * ((float) Math.PI / 180F));
                    this.level.addParticle(new ItemParticleData(ParticleTypes.ITEM, itemstack), this.getX() + this.getLookAngle().x / 2.0D, this.getY(), this.getZ() + this.getLookAngle().z / 2.0D, vec3d.x, vec3d.y + 0.05D, vec3d.z);
                }
            }
        } else {
            super.handleEntityEvent(id);
        }

    }

    public static AttributeModifierMap.MutableAttribute attributes() {
        return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 6.0D)
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

    // Cannot add conditional goals in RegisterGoals as it is final and called during the MobEntity super.
    protected void addGoalsAfterConstructor() {
        if (this.level.isClientSide())
            return;

        for (PrioritizedGoal goal : getGoals()) {
            this.goalSelector.addGoal(goal.getPriority(), goal.getGoal());
        }
    }

    public List<PrioritizedGoal> getGoals() {
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
    public List<PrioritizedGoal> getTamedGoals() {
        List<PrioritizedGoal> list = new ArrayList<>();
        list.add(new PrioritizedGoal(1, new GetUnstuckGoal(this, () -> this.isStuck, stuck -> {
            this.isStuck = stuck;
            return null;
        })));
        list.add(new PrioritizedGoal(1, new FindItem(this)));
        list.add(new PrioritizedGoal(2, new ForageManaBerries(this)));
        list.add(new PrioritizedGoal(3, new StoreItemGoal(this)));
        list.add(new PrioritizedGoal(3, new TakeItemGoal(this)));
        list.add(new PrioritizedGoal(8, new LookAtGoal(this, PlayerEntity.class, 3.0F, 0.01F)));
        list.add(new PrioritizedGoal(8, new NonHoggingLook(this, MobEntity.class, 3.0F, 0.01f)));
        list.add(new PrioritizedGoal(0, new SwimGoal(this)));
//        list.add(new PrioritizedGoal(4, new GoBackHomeGoal(this, this::getHome, 5, () ->
//                (this.getHeldStack() == null || this.getHeldStack().isEmpty()) &&
//                        world.getEntitiesWithinAABB(ItemEntity.class, getBoundingBox().grow(8.0D, 6, 8.0D), (itemEntity) -> !itemEntity.cannotPickup() && itemEntity.isAlive() && isValidItem(itemEntity.getItem())).isEmpty())));
//        // Roam back in case we have no item and are far from home.
      //  list.add(new PrioritizedGoal(1, new GoBackHomeGoal(this, this::getHome, 25, () -> (this.getHeldStack() == null || this.getHeldStack().isEmpty()))));
        return list;
    }

    public List<PrioritizedGoal> getUntamedGoals() {
        List<PrioritizedGoal> list = new ArrayList<>();
        list.add(new PrioritizedGoal(1, new FindItem(this)));
        list.add(new PrioritizedGoal(4, new LookAtGoal(this, PlayerEntity.class, 3.0F, 0.02F)));
        list.add(new PrioritizedGoal(4, new LookAtGoal(this, MobEntity.class, 8.0F)));
        list.add(new PrioritizedGoal(3, new WaterAvoidingRandomWalkingGoal(this, 1.0D)));
        list.add(new PrioritizedGoal(2, new AvoidPlayerUntamedGoal(this, PlayerEntity.class, 16.0F, 1.6D, 1.4D)));
        list.add(new PrioritizedGoal(0, new SwimGoal(this)));
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

    public AxisAlignedBB getAABB(){
        if(cachedAAB == null || lastAABBCalc >= 60) {
            cachedAAB = new AxisAlignedBB(blockPosition()).inflate(8);
            lastAABBCalc = 0;
        }
        return cachedAAB;
    }

    @Override
    protected ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        if (hand != Hand.MAIN_HAND || player.getCommandSenderWorld().isClientSide || !isTamed())
            return ActionResultType.SUCCESS;

        ItemStack stack = player.getItemInHand(hand);

        if (player.getMainHandItem().getItem().is(Tags.Items.DYES)) {
            DyeColor color = DyeColor.getColor(stack);
            if(color == null || this.entityData.get(COLOR).equals(color.getName()) || !Arrays.asList(carbyColors).contains(color.getName()))
                return ActionResultType.SUCCESS;
            this.entityData.set(COLOR, color.getName());
            player.getMainHandItem().shrink(1);
            return ActionResultType.SUCCESS;
        }

        if (player.getMainHandItem().isEmpty() && this.isTamed()) {
            StringBuilder status = new StringBuilder();
            if (whitelist && allowedItems != null) {
                status.append(new TranslationTextComponent("ars_nouveau.carbuncle.whitelist").getString());
                for (ItemStack i : allowedItems) {
                    status.append(i.getHoverName().getString());
                }
            } else if (blacklist && allowedItems != null) {
                status.append(new TranslationTextComponent("ars_nouveau.carbuncle.blacklist").getString());
                for (ItemStack i : ignoreItems) {
                    status.append(i.getHoverName().getString());
                }
            }
            if (!status.toString().isEmpty())
                PortUtil.sendMessage(player, status.toString());
        }

        if (!(stack.getItem() instanceof ItemScroll) || !stack.hasTag())
            return ActionResultType.FAIL;
        if (stack.getItem() == ItemsRegistry.ALLOW_ITEM_SCROLL) {
            List<ItemStack> items = ItemsRegistry.ALLOW_ITEM_SCROLL.getItems(stack);
            if (!items.isEmpty()) {
                this.allowedItems = ItemsRegistry.ALLOW_ITEM_SCROLL.getItems(stack);
                whitelist = true;
                blacklist = false;
                PortUtil.sendMessage(player, new TranslationTextComponent("ars_nouveau.allow_set"));
            }
            return ActionResultType.SUCCESS;
        }

        if (stack.getItem() == ItemsRegistry.DENY_ITEM_SCROLL) {
            List<ItemStack> items = ItemsRegistry.DENY_ITEM_SCROLL.getItems(stack);
            if (!items.isEmpty()) {
                this.ignoreItems = ItemsRegistry.DENY_ITEM_SCROLL.getItems(stack);
                whitelist = false;
                blacklist = true;
                PortUtil.sendMessage(player, new TranslationTextComponent("ars_nouveau.ignore_set"));
            }
        }

        return ActionResultType.SUCCESS;
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
        this.entityData.define(HOP, false);
        this.entityData.define(TO_POS, 0);
        this.entityData.define(FROM_POS, 0);
        this.entityData.define(COLOR, COLORS.ORANGE.name());
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    public void setHeldStack(ItemStack stack) {
        this.setItemSlot(EquipmentSlotType.MAINHAND, stack);
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
            ParticleUtil.spawnPoof((ServerWorld) level, blockPosition());
            this.remove();
        }
        return this.isTamed();
    }

    private boolean setBehaviors;

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("held"))
            setHeldStack(ItemStack.of((CompoundNBT) tag.get("held")));
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


     //   setFromPos(NBTUtil.getBlockPos(tag, "from"));
//        if (getToPos().equals(new BlockPos(0, 0, 0)))
//            setToPos(null);
//        if (getFromPos().equals(new BlockPos(0, 0, 0)))
//            setFromPos(null);
        backOff = tag.getInt("backoff");
        tamingTime = tag.getInt("taming_time");
        whitelist = tag.getBoolean("whitelist");
        blacklist = tag.getBoolean("blacklist");
        this.entityData.set(HOP, tag.getBoolean("hop"));

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
    }


    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        if (getHeldStack() != null) {
            CompoundNBT itemTag = new CompoundNBT();
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

//        if (getToPos() != null)
//            NBTUtil.storeBlockPos(tag, "to", getToPos());
//        if (getFromPos() != null)
//            NBTUtil.storeBlockPos(tag, "from", getFromPos());
        tag.putInt("backoff", backOff);
        tag.putBoolean("tamed", this.entityData.get(TAMED));
        tag.putInt("taming_time", tamingTime);
        tag.putBoolean("hop", this.entityData.get(HOP));
        tag.putBoolean("whitelist", whitelist);
        tag.putBoolean("blacklist", blacklist);
        if (allowedItems != null && !allowedItems.isEmpty())
            NBTUtil.writeItems(tag, "allowed_", allowedItems);

        if (ignoreItems != null && !ignoreItems.isEmpty())
            NBTUtil.writeItems(tag, "ignored_", ignoreItems);
        tag.putBoolean("stuck", isStuck);
        tag.putString("color", this.entityData.get(COLOR));
    }

    public void removeGoals() {
        this.goalSelector.availableGoals = new LinkedHashSet<>();
    }

    @Override
    public List<String> getTooltip() {
        List<String> toolTip = new ArrayList<>();
        if(!isTamed())
            return toolTip;
        toolTip.add(new TranslationTextComponent("ars_nouveau.carbuncle.storing", this.entityData.get(TO_POS)).getString());
        toolTip.add(new TranslationTextComponent("ars_nouveau.carbuncle.taking", this.entityData.get(FROM_POS)).getString());
        return toolTip;
    }


    private SortPref canDepositItem(TileEntity tile, ItemStack stack) {
        SortPref pref = SortPref.LOW;
        if (tile == null || stack == null || stack.isEmpty())
            return SortPref.INVALID;
        for (ItemFrameEntity i : level.getEntitiesOfClass(ItemFrameEntity.class, new AxisAlignedBB(tile.getBlockPos()).inflate(1))) {
            // Check if these frames are attached to the tile
            TileEntity adjTile = level.getBlockEntity(i.blockPosition().relative(i.getDirection().getOpposite()));
            if(adjTile == null || !adjTile.equals(tile))
                continue;
            CompoundNBT tag = i.getItem().getTag();
            if (i.getItem().isEmpty())
                continue;

            if (i.getItem().getItem().getItem() == ItemsRegistry.ALLOW_ITEM_SCROLL) {
                if (!ItemScroll.containsItem(stack, tag))
                    return SortPref.INVALID;
                else
                    pref = SortPref.HIGH;
            } else if (i.getItem().getItem().getItem() == ItemsRegistry.DENY_ITEM_SCROLL) {
                if (ItemScroll.containsItem(stack, tag))
                    return SortPref.INVALID;
                else
                    pref = SortPref.LOW;
            }else if (i.getItem().getItem() != stack.getItem()) {
                return SortPref.INVALID;
            }else if(i.getItem().getItem() == stack.getItem())
                pref = SortPref.HIGH;
        }
        IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if(handler == null)
            return SortPref.INVALID;
        return !ItemStack.matches(ItemHandlerHelper.insertItemStacked(handler, stack.copy(), true), stack) ? pref : SortPref.INVALID;
    }

    @Override
    protected int getExperienceReward(PlayerEntity player) {
        return 0;
    }

    public BlockPos getValidStorePos(ItemStack stack){
        BlockPos returnPos = null;
        if(TO_LIST == null)
            return returnPos;
        for(BlockPos b : TO_LIST){
            SortPref pref = canDepositItem(level.getBlockEntity(b), stack);
            if (pref == SortPref.HIGH)
               return b;
            else if(pref == SortPref.LOW)
                returnPos = b;
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






    public boolean isValidItem(ItemStack stack){

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
        if(blacklist){
            for(ItemStack s : ignoreItems)
                if(s.sameItem(stack))
                    return false;
        }
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

    public enum COLORS {
        ORANGE,
        PURPLE,
        GREEN
    }

    public enum SortPref {
        HIGH,
        LOW,
        INVALID
    }
}
