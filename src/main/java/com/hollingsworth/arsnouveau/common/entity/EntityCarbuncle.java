package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.common.event.OpenChestEvent;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.Path;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayerFactory;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.animation.controller.EntityAnimationController;
import software.bernie.geckolib.entity.IAnimatedEntity;
import software.bernie.geckolib.event.AnimationTestEvent;
import software.bernie.geckolib.manager.EntityAnimationManager;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class EntityCarbuncle extends CreatureEntity implements IAnimatedEntity {

    public BlockPos fromPos;
    public BlockPos toPos;

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
        manager.addAnimationController(walkController);
        manager.addAnimationController(idleController);
    }

    public EntityCarbuncle(World world, boolean tamed){
        super(ModEntities.ENTITY_CARBUNCLE_TYPE,world);
        this.setTamed(tamed);
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
        }else{
            return false;
        }
        return true;
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
                    double d0 = getPosX(); //+ world.rand.nextFloat();
                    double d1 = getPosY()+0.1;//+ world.rand.nextFloat() ;
                    double d2 = getPosZ()  ; //+ world.rand.nextFloat();
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

    /**
     * Handler for {@link World#setEntityState}
     */
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 45) {
            ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
            if (!itemstack.isEmpty()) {
                for(int i = 0; i < 8; ++i) {
                    Vec3d vec3d = (new Vec3d(((double)this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D)).rotatePitch(-this.rotationPitch * ((float)Math.PI / 180F)).rotateYaw(-this.rotationYaw * ((float)Math.PI / 180F));
                    this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, itemstack), this.getPosX() + this.getLookVec().x / 2.0D, this.getPosY(), this.getPosZ() + this.getLookVec().z / 2.0D, vec3d.x, vec3d.y + 0.05D, vec3d.z);
                }
            }
        } else {
            super.handleStatusUpdate(id);
        }

    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.2F);
    }

    @Override
    protected void updateEquipmentIfNeeded(ItemEntity itemEntity) {
        if(this.getHeldStack().isEmpty()){
            setHeldStack(itemEntity.getItem());
            itemEntity.remove();
            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_ITEM_PICKUP, this.getSoundCategory(),1.0F, 1.0F);
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(10, new FindItem());
        this.goalSelector.addGoal(8, new StoreItemGoal());
        this.goalSelector.addGoal(8, new TakeItemGoal());
        this.goalSelector.addGoal(3, new LookAtGoal(this, PlayerEntity.class, 3.0F, 0.02F));
        this.goalSelector.addGoal(3, new LookAtGoal(this, MobEntity.class, 8.0F));
        this.goalSelector.addGoal(3, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new AvoidPlayerUntamedGoal(this, PlayerEntity.class, 16.0F, 1.6D, 1.4D));
//        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
    }


    public class AvoidPlayerUntamedGoal extends AvoidEntityGoal<LivingEntity>{

        public AvoidPlayerUntamedGoal(CreatureEntity entityIn, Class classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
            super(entityIn, classToAvoidIn, avoidDistanceIn, farSpeedIn, nearSpeedIn ,(living) -> {
                return !(living.getHeldItemMainhand().getItem() == Items.GOLD_NUGGET);
            });
        }

        @Override
        public boolean shouldExecute() {
            if(EntityCarbuncle.this.isTamed())
                return false;
            return super.shouldExecute();
        }
    }

    public class FindItem extends Goal{
        private final Predicate<ItemEntity> TRUSTED_TARGET_SELECTOR = (itemEntity) -> {
            return !itemEntity.cannotPickup() && itemEntity.isAlive();
        };

        private final Predicate<ItemEntity> NONTAMED_TARGET_SELECTOR = (itemEntity -> {
            return !itemEntity.cannotPickup() && itemEntity.isAlive() && itemEntity.getItem().getItem() == Items.GOLD_NUGGET;
        });

        public FindItem(){
            this.setMutexFlags(EnumSet.of(Flag.MOVE));
        }

        public Predicate<ItemEntity> getFinderItems(){
            return EntityCarbuncle.this.isTamed() ? TRUSTED_TARGET_SELECTOR : NONTAMED_TARGET_SELECTOR;
        }

        @Override
        public boolean shouldExecute() {
            List<ItemEntity> list = EntityCarbuncle.this.world.getEntitiesWithinAABB(ItemEntity.class, EntityCarbuncle.this.getBoundingBox().grow(8.0D, 6.0D, 8.0D), getFinderItems());


            return !list.isEmpty() && EntityCarbuncle.this.getHeldStack().isEmpty();
        }



        @Override
        public void startExecuting() {
            super.startExecuting();
            List<ItemEntity> list = EntityCarbuncle.this.world.getEntitiesWithinAABB(ItemEntity.class, EntityCarbuncle.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), getFinderItems());
            ItemStack itemstack = EntityCarbuncle.this.getHeldStack();

            if (itemstack.isEmpty() && !list.isEmpty()) {
                EntityCarbuncle.this.pathToTarget(list.get(0), 1.2f);
                EntityCarbuncle.this.dataManager.set(HOP, true);
            }
        }

        @Override
        public void tick() {
            super.tick();

            List<ItemEntity> list = EntityCarbuncle.this.world.getEntitiesWithinAABB(ItemEntity.class, EntityCarbuncle.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), getFinderItems());
            ItemStack itemstack = EntityCarbuncle.this.getHeldStack();
            if (itemstack.isEmpty() && !list.isEmpty()) {
                EntityCarbuncle.this.pathToTarget(list.get(0), 1.2f);
                EntityCarbuncle.this.dataManager.set(HOP, true);
            }
        }
    }

    public void pathToTarget(Entity entity, double speed){
        Path path = this.getNavigator().getPathToEntity(entity, 0);
        this.getNavigator().setPath(path, speed);
    }

    public class WaterAvoidingRandomWalkingGoal extends RandomWalkingGoal {
        protected final float probability;

        public WaterAvoidingRandomWalkingGoal(CreatureEntity creature, double speedIn) {
            this(creature, speedIn, 0.001F);
        }

        public WaterAvoidingRandomWalkingGoal(CreatureEntity creature, double speedIn, float probabilityIn) {
            super(creature, speedIn);
            this.probability = probabilityIn;
        }

        @Override
        public void tick() {
            super.tick();
        }

        @Nullable
        protected Vec3d getPosition() {
            if (this.creature.isInWaterOrBubbleColumn()) {
                Vec3d vec3d = RandomPositionGenerator.getLandPos(this.creature, 15, 7);
                return vec3d == null ? super.getPosition() : vec3d;
            } else {
                return this.creature.getRNG().nextFloat() >= this.probability ? RandomPositionGenerator.getLandPos(this.creature, 10, 7) : super.getPosition();
            }
        }

        @Override
        public boolean shouldContinueExecuting() {
            if(isTamed())
                return false;

            return super.shouldContinueExecuting();
        }

        @Override
        public boolean shouldExecute() {
            if(isTamed())
                return false;
            if( super.shouldExecute()){
                return true;
            }
            return false;
        }
    }

    public class StoreItemGoal extends Goal {

        public StoreItemGoal(){

            this.setMutexFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public void startExecuting() {
            super.startExecuting();
            if(EntityCarbuncle.this.toPos != null && !EntityCarbuncle.this.getHeldStack().isEmpty())
                EntityCarbuncle.this.getNavigator().tryMoveToXYZ(EntityCarbuncle.this.toPos.getX(),EntityCarbuncle.this.toPos.getY(),EntityCarbuncle.this.toPos.getZ(), 1.2D);
        }
        private IntStream func_213972_a(IInventory p_213972_0_, Direction p_213972_1_) {
            return p_213972_0_ instanceof ISidedInventory ? IntStream.of(((ISidedInventory)p_213972_0_).getSlotsForFace(p_213972_1_)) : IntStream.range(0, p_213972_0_.getSizeInventory());
        }

        private boolean isInventoryFull(IInventory inventoryIn, Direction side) {
            return func_213972_a(inventoryIn, side).allMatch((p_213970_1_) -> {
                ItemStack itemstack = inventoryIn.getStackInSlot(p_213970_1_);
                return itemstack.getCount() >= itemstack.getMaxStackSize();
            });
        }



        @Override
        public void tick() {
            super.tick();
            if(!EntityCarbuncle.this.getHeldStack().isEmpty() && EntityCarbuncle.this.toPos != null && BlockUtil.distanceFrom(EntityCarbuncle.this.getPosition(), EntityCarbuncle.this.toPos) < 1.25D){
                World world = EntityCarbuncle.this.world;
                if(world.getTileEntity(EntityCarbuncle.this.toPos) instanceof IInventory){
                    ItemStack oldStack = new ItemStack(EntityCarbuncle.this.getHeldStack().getItem(), EntityCarbuncle.this.getHeldStack().getCount());

                    IInventory i = (IInventory) world.getTileEntity(EntityCarbuncle.this.toPos);
                    ItemStack left = HopperTileEntity.putStackInInventoryAllSlots(null, i, EntityCarbuncle.this.getHeldStack(), null);
                    if(left.equals(oldStack)) {
                        return;
                    }
                    if(world instanceof ServerWorld){
                        OpenChestEvent event = new OpenChestEvent(FakePlayerFactory.getMinecraft((ServerWorld) world), toPos, 20);
                        event.open();
                        EventQueue.getInstance().addEvent(event);
                    }
                    EntityCarbuncle.this.setHeldStack(left);
//                    EntityCarbuncle.this.world.playSound(null, EntityCarbuncle.this.getPosX(), EntityCarbuncle.this.getPosY(), EntityCarbuncle.this.getPosZ(), SoundEvents.ENTITY_ITEM_PICKUP, EntityCarbuncle.this.getSoundCategory(),1.0F, 1.0F);
                    EntityCarbuncle.this.backOff = 20;

                    EntityCarbuncle.this.dataManager.set(HOP, false);
                    return;
                }
            }

            if(EntityCarbuncle.this.toPos != null && !EntityCarbuncle.this.getHeldStack().isEmpty()) {
                EntityCarbuncle.this.getNavigator().tryMoveToXYZ(EntityCarbuncle.this.toPos.getX(), EntityCarbuncle.this.toPos.getY(), EntityCarbuncle.this.toPos.getZ(), 1.2D);
                EntityCarbuncle.this.dataManager.set(HOP, true);
            }
        }

        @Override
        public boolean shouldContinueExecuting() {
            return  EntityCarbuncle.this.isTamed() && EntityCarbuncle.this.getHeldStack() != null && !EntityCarbuncle.this.getHeldStack().isEmpty() && EntityCarbuncle.this.backOff == 0;
        }

        @Override
        public boolean shouldExecute() {
            return  EntityCarbuncle.this.isTamed() && EntityCarbuncle.this.getHeldStack() != null && !EntityCarbuncle.this.getHeldStack().isEmpty() && EntityCarbuncle.this.backOff == 0;
        }
    }


    public class TakeItemGoal extends Goal{

        public TakeItemGoal(){
            this.setMutexFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public void startExecuting() {
            super.startExecuting();
            if( EntityCarbuncle.this.isTamed() && EntityCarbuncle.this.fromPos != null && EntityCarbuncle.this.getHeldStack().isEmpty())
                EntityCarbuncle.this.getNavigator().tryMoveToXYZ(EntityCarbuncle.this.fromPos.getX(),EntityCarbuncle.this.fromPos.getY(),EntityCarbuncle.this.fromPos.getZ(), 1.2D);
        }

        @Override
        public void tick() {
            super.tick();

            if(EntityCarbuncle.this.getHeldStack().isEmpty() && EntityCarbuncle.this.fromPos != null && BlockUtil.distanceFrom(EntityCarbuncle.this.getPosition(), EntityCarbuncle.this.fromPos) < 1.25D){
                World world = EntityCarbuncle.this.world;
                if(world.getTileEntity(EntityCarbuncle.this.fromPos) instanceof IInventory){
                    IInventory i = (IInventory) world.getTileEntity(EntityCarbuncle.this.fromPos);
                    for(int j = 0; j < i.getSizeInventory(); j++){
                        if(!i.getStackInSlot(j).isEmpty()){

                            EntityCarbuncle.this.setHeldStack(i.removeStackFromSlot(j));

                            EntityCarbuncle.this.world.playSound(null, EntityCarbuncle.this.getPosX(), EntityCarbuncle.this.getPosY(), EntityCarbuncle.this.getPosZ(),
                                    SoundEvents.ENTITY_ITEM_PICKUP, EntityCarbuncle.this.getSoundCategory(),1.0F, 1.0F);

                            if(world instanceof ServerWorld){
                                OpenChestEvent event = new OpenChestEvent(FakePlayerFactory.getMinecraft((ServerWorld) world), fromPos, 20);
                                event.open();
                                EventQueue.getInstance().addEvent(event);
                            }
                            break;
                        }
                    }
                    return;
                }
            }

            if(EntityCarbuncle.this.fromPos != null && EntityCarbuncle.this.getHeldStack().isEmpty()) {
                EntityCarbuncle.this.getNavigator().tryMoveToXYZ(EntityCarbuncle.this.fromPos.getX(), EntityCarbuncle.this.fromPos.getY(), EntityCarbuncle.this.fromPos.getZ(), 1.2D);
            }
        }

        @Override
        public boolean shouldContinueExecuting() {
            return EntityCarbuncle.this.getHeldStack() != null && EntityCarbuncle.this.getHeldStack().isEmpty() && EntityCarbuncle.this.backOff == 0 && EntityCarbuncle.this.isTamed();
        }

        @Override
        public boolean shouldExecute() {
            return EntityCarbuncle.this.getHeldStack() != null && EntityCarbuncle.this.getHeldStack().isEmpty() && EntityCarbuncle.this.backOff == 0 && EntityCarbuncle.this.isTamed();
        }
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
    protected boolean processInteract(PlayerEntity player, Hand hand) {
        System.out.println(this.getHeldStack());
        System.out.println(this.toPos);
        System.out.println(this.fromPos);
        System.out.println(this.isTamed());
        return false;
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

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        if(tag.contains("held"))
            setHeldStack(ItemStack.read((CompoundNBT)tag.get("held")));
        toPos = NBTUtil.getBlockPos(tag, "to");
        fromPos = NBTUtil.getBlockPos(tag, "from");
        backOff = tag.getInt("backoff");
        setTamed(tag.getBoolean("tamed"));
        tamingTime = tag.getInt("taming_time");
        this.dataManager.set(HOP, tag.getBoolean("hop"));
    }
    public void setHeldStack(ItemStack stack){
//        this.dataManager.set(HELD_ITEM,stack);
        this.setItemStackToSlot(EquipmentSlotType.MAINHAND, stack);
    }

    public ItemStack getHeldStack(){
        return this.getHeldItemMainhand();
//        return this.dataManager.get(HELD_ITEM);
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
        tag.putBoolean("tamed", isTamed());
        tag.putInt("taming_time", tamingTime);
        tag.putBoolean("hop", this.dataManager.get(HOP));
    }
}
