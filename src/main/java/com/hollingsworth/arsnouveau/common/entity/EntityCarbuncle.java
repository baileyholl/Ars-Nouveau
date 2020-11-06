package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.goal.GoBackHomeGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.AvoidPlayerUntamedGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.FindItem;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StoreItemGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.TakeItemGoal;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.animation.controller.EntityAnimationController;
import software.bernie.geckolib.entity.IAnimatedEntity;
import software.bernie.geckolib.event.AnimationTestEvent;
import software.bernie.geckolib.manager.EntityAnimationManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;


public class EntityCarbuncle extends CreatureEntity implements IAnimatedEntity, IDispellable, ITooltipProvider, IWandable {


    public BlockPos fromPos;
    public BlockPos toPos;
    public List<ItemStack> allowedItems; // Items the carbuncle is allowed to take
    public List<ItemStack> ignoreItems; // Items the carbuncle will not take
    public boolean whitelist;
    public boolean blacklist;
    public static final DataParameter<ItemStack> HELD_ITEM = EntityDataManager.createKey(EntityCarbuncle.class, DataSerializers.ITEMSTACK);
    public static final DataParameter<Boolean> TAMED = EntityDataManager.createKey(EntityCarbuncle.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> HOP = EntityDataManager.createKey(EntityCarbuncle.class, DataSerializers.BOOLEAN);
    public int backOff; // Used to stop inventory store/take spam when chests are full or empty.
    public int tamingTime;

    EntityAnimationManager manager = new EntityAnimationManager();
    EntityAnimationController<EntityCarbuncle> walkController = new EntityAnimationController<>(this, "walkController", 20, this::animationPredicate);
    EntityAnimationController<EntityCarbuncle> idleController = new EntityAnimationController<>(this, "idleController", 20, this::idlePredicate);

    public EntityCarbuncle(EntityType<EntityCarbuncle> entityCarbuncleEntityType, World world) {
        super(entityCarbuncleEntityType, world);
        setupAnimations();
        addGoalsAfterConstructor();
    }

    public EntityCarbuncle(World world, boolean tamed){
        super(ModEntities.ENTITY_CARBUNCLE_TYPE,world);
        this.setTamed(tamed);
        setupAnimations();
        addGoalsAfterConstructor();
    }
    public void setupAnimations(){
        manager.addAnimationController(walkController);
        manager.addAnimationController(idleController);
    }

    private <E extends Entity> boolean idlePredicate(AnimationTestEvent<E> event) {
        if(world.getGameTime() % 20 == 0 && world.rand.nextInt(3) == 0 && !this.dataManager.get(HOP)){
            manager.setAnimationSpeed(3f);
            idleController.setAnimation(new AnimationBuilder().addAnimation("idle"));

        }
        return true;
    }
    private <E extends Entity> boolean animationPredicate(AnimationTestEvent<E> event) {
        if(this.dataManager.get(HOP)){
            manager.setAnimationSpeed(5f);
            walkController.setAnimation(new AnimationBuilder().addAnimation("hop"));
            return true;
        }
        return false;
    }
    @Override
    public EntityAnimationManager getAnimationManager() {
        return manager;
    }

    public boolean isTamed(){
        return this.dataManager.get(TAMED);
    }

    public void setTamed(boolean tamed){
        this.dataManager.set(TAMED,tamed);
    }


    public void attemptTame(){
        if(!isTamed() && this.getHeldStack().getItem() == Items.GOLD_NUGGET){
            tamingTime++;
            if(tamingTime % 20 == 0 && !world.isRemote())
                Networking.sendToNearby(world, this, new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, getPosition()));

            if(tamingTime > 60 && !world.isRemote) {
                ItemStack stack = new ItemStack(ItemsRegistry.carbuncleShard, 1 + world.rand.nextInt(2));
                world.addEntity(new ItemEntity(world, getPosX(), getPosY() + 0.5, getPosZ(), stack));
                this.remove(false);
                world.playSound(null, getPosX(), getPosY(), getPosZ(), SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.NEUTRAL, 1f, 1f );
            }
            else if (tamingTime > 55 && world.isRemote){
                for(int i =0; i < 10; i++){
                    double d0 = getPosX();
                    double d1 = getPosY() + 0.1;
                    double d2 = getPosZ();
                    world.addParticle(ParticleTypes.END_ROD, d0, d1, d2, (world.rand.nextFloat() * 1 - 0.5)/3, (world.rand.nextFloat() * 1 - 0.5)/3, (world.rand.nextFloat() * 1 - 0.5)/3);
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(!world.isRemote){
            if(this.navigator.noPath()){
                EntityCarbuncle.this.dataManager.set(HOP, false);
            }else{
                EntityCarbuncle.this.dataManager.set(HOP, true);
            }
        }

        if(this.backOff > 0 && !world.isRemote)
            this.backOff--;
        if(this.dead)
            return;

        if(this.getHeldStack().isEmpty() && !world.isRemote){

            for(ItemEntity itementity : this.world.getEntitiesWithinAABB(ItemEntity.class, this.getBoundingBox().grow(1.0D, 0.0D, 1.0D))) {
                if (!itementity.removed && !itementity.getItem().isEmpty() && !itementity.cannotPickup()) {
                    if(!isTamed() && itementity.getItem().getItem() != Items.GOLD_NUGGET)
                        return;
                    this.updateEquipmentIfNeeded(itementity);
                    this.dataManager.set(HOP, false);
                }
            }
        }
        attemptTame();
    }


    @Override
    public void onWanded(PlayerEntity playerEntity) {

    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, PlayerEntity playerEntity) {
        if(storedPos == null)
            return;
        if(world.getTileEntity(storedPos) instanceof IInventory){
            PortUtil.sendMessage(playerEntity, "Carbuncle will store items here.");
            toPos = storedPos;
        }
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, PlayerEntity playerEntity) {
        if(storedPos == null)
            return;

        if(world.getTileEntity(storedPos)  instanceof IInventory){
            PortUtil.sendMessage(playerEntity, "Carbuncle take from this inventory.");
            fromPos = storedPos;
        }
    }

    /**
     * Handler for {@link World#setEntityState}
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 45) {
            ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
            if (!itemstack.isEmpty()) {
                for(int i = 0; i < 8; ++i) {
                    Vector3d vec3d = (new Vector3d(((double)this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D)).rotatePitch(-this.rotationPitch * ((float)Math.PI / 180F)).rotateYaw(-this.rotationYaw * ((float)Math.PI / 180F));
                    this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, itemstack), this.getPosX() + this.getLookVec().x / 2.0D, this.getPosY(), this.getPosZ() + this.getLookVec().z / 2.0D, vec3d.x, vec3d.y + 0.05D, vec3d.z);
                }
            }
        } else {
            super.handleStatusUpdate(id);
        }

    }



    public static AttributeModifierMap.MutableAttribute attributes() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 6.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2d);
    }


    @Override
    protected void updateEquipmentIfNeeded(ItemEntity itemEntity) {
        if(this.getHeldStack().isEmpty() && TakeItemGoal.isValidItem(this, itemEntity.getItem())){
            setHeldStack(itemEntity.getItem());
            itemEntity.remove();
            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_ITEM_PICKUP, this.getSoundCategory(),1.0F, 1.0F);
        }
    }





    // Cannot add conditional goals in RegisterGoals as it is final and called during the MobEntity super.
    protected void addGoalsAfterConstructor(){
        if(this.world.isRemote())
            return;

        for(PrioritizedGoal goal : getGoals()){
            this.goalSelector.addGoal(goal.getPriority(), goal.getGoal());
        }
    }
    public List<PrioritizedGoal> getGoals(){
        return Boolean.TRUE.equals(this.dataManager.get(TAMED)) ? getTamedGoals() : getUntamedGoals();
    }


    public BlockPos getHome(){
        if(this.fromPos == null && toPos != null)
            return toPos;
        if(this.toPos == null && this.fromPos != null)
            return fromPos;
        if(toPos != null)
            return fromPos;
        return null;
    }

    //MOJANG MAKES THIS SO CURSED WHAT THE HECK
    public List<PrioritizedGoal> getTamedGoals(){
        List<PrioritizedGoal> list = new ArrayList<>();
        list.add(new PrioritizedGoal(2, new FindItem(this)));
        list.add(new PrioritizedGoal(3, new StoreItemGoal(this)));
        list.add(new PrioritizedGoal(3, new TakeItemGoal(this)));
        list.add(new PrioritizedGoal(5, new LookAtGoal(this, PlayerEntity.class, 3.0F, 0.02F)));
        list.add(new PrioritizedGoal(5, new LookAtGoal(this, MobEntity.class, 8.0F)));
        list.add(new PrioritizedGoal(0, new SwimGoal(this)));
        // Roam back in case we have no item and are far from home.
        list.add(new PrioritizedGoal(1, new GoBackHomeGoal(this, this::getHome, 25, ()->(this.getHeldStack() == null || this.getHeldStack().isEmpty()))));
        return list;
    }

    public List<PrioritizedGoal> getUntamedGoals(){
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
    public void onDeath(DamageSource source) {
        if(!world.isRemote && isTamed()){
            ItemStack stack = new ItemStack(ItemsRegistry.carbuncleCharm);
            world.addEntity(new ItemEntity(world, getPosX(), getPosY(), getPosZ(), stack));
            if(this.getHeldStack() != null)
                world.addEntity(new ItemEntity(world, getPosX(), getPosY(), getPosZ(), this.getHeldStack()));
        }
        super.onDeath(source);
    }

    @Override
    protected ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        if(hand != Hand.MAIN_HAND || player.getEntityWorld().isRemote)
            return ActionResultType.SUCCESS;

        ItemStack stack = player.getHeldItem(hand);

        if(!(stack.getItem() instanceof ItemScroll) || !stack.hasTag())
            return ActionResultType.FAIL;
        if(stack.getItem() == ItemsRegistry.ALLOW_ITEM_SCROLL){
            List<ItemStack>  items = ItemsRegistry.ALLOW_ITEM_SCROLL.getItems(stack);
            if(!items.isEmpty()) {
                this.allowedItems = ItemsRegistry.ALLOW_ITEM_SCROLL.getItems(stack);
                whitelist = true;
                blacklist = false;
                PortUtil.sendMessage(player, new TranslationTextComponent("ars_nouveau.allow_set"));
            }
            return ActionResultType.SUCCESS;
        }

        if(stack.getItem() == ItemsRegistry.DENY_ITEM_SCROLL){
            List<ItemStack>  items = ItemsRegistry.DENY_ITEM_SCROLL.getItems(stack);
            if(!items.isEmpty()) {
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
    protected void registerData() {
        super.registerData();
        this.dataManager.register(HELD_ITEM, ItemStack.EMPTY);
        this.dataManager.register(TAMED, false);
        this.dataManager.register(HOP, false);
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    private boolean setBehaviors;
    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);
        if(tag.contains("held"))
            setHeldStack(ItemStack.read((CompoundNBT)tag.get("held")));
        toPos = NBTUtil.getBlockPos(tag, "to");
        fromPos = NBTUtil.getBlockPos(tag, "from");
        if(toPos.equals(new BlockPos(0,0,0)))
            toPos = null;
        if(fromPos.equals(new BlockPos(0,0,0)))
            fromPos = null;
        backOff = tag.getInt("backoff");
        tamingTime = tag.getInt("taming_time");
        whitelist = tag.getBoolean("whitelist");
        blacklist = tag.getBoolean("blacklist");
        this.dataManager.set(HOP, tag.getBoolean("hop"));

        // Remove goals and read them AFTER our tamed param is set because we can't ACCESS THEM OTHERWISE
        if(!setBehaviors)
            this.removeGoals();
        this.dataManager.set(TAMED, tag.getBoolean("tamed"));
        if(!setBehaviors) {
            this.addGoalsAfterConstructor();
            setBehaviors = true;
        }
        allowedItems = NBTUtil.readItems(tag, "allowed_");
        ignoreItems = NBTUtil.readItems(tag, "ignored_");
    }

    public void setHeldStack(ItemStack stack){
        this.setItemStackToSlot(EquipmentSlotType.MAINHAND, stack);
    }

    public ItemStack getHeldStack(){
        return this.getHeldItemMainhand();
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if(this.removed)
            return false;

        if(!world.isRemote && isTamed()){
            ItemStack stack = new ItemStack(ItemsRegistry.carbuncleCharm);
            world.addEntity(new ItemEntity(world, getPosX(), getPosY(), getPosZ(), stack.copy()));
            stack = getHeldStack();
            world.addEntity(new ItemEntity(world, getPosX(), getPosY(), getPosZ(), stack));
            ParticleUtil.spawnPoof((ServerWorld)world, getPosition());
            this.remove();
        }
        return this.isTamed();
    }

    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
        if(getHeldStack() != null) {
            CompoundNBT itemTag = new CompoundNBT();
            getHeldStack().write(itemTag);
            tag.put("held", itemTag);
        }
        if(toPos != null)
            NBTUtil.storeBlockPos(tag, "to",toPos);
        if(fromPos != null)
            NBTUtil.storeBlockPos(tag, "from",fromPos);
        tag.putInt("backoff", backOff);
        tag.putBoolean("tamed",  this.dataManager.get(TAMED));
        tag.putInt("taming_time", tamingTime);
        tag.putBoolean("hop", this.dataManager.get(HOP));
        tag.putBoolean("whitelist",whitelist);
        tag.putBoolean("blacklist", blacklist);
        if(allowedItems != null && !allowedItems.isEmpty())
            NBTUtil.writeItems(tag,  "allowed_", allowedItems);

        if(ignoreItems != null && !ignoreItems.isEmpty())
            NBTUtil.writeItems(tag,  "ignored_", ignoreItems);

    }

    public void removeGoals(){
        this.goalSelector.goals = new LinkedHashSet<>();
    }

    @Override
    public List<String> getTooltip() {

        return new ArrayList<>();
    }
}
