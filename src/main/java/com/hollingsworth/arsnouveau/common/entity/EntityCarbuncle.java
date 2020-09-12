package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.Path;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class EntityCarbuncle extends CreatureEntity {

    public BlockPos fromPos;
    public BlockPos toPos;
    public static final DataParameter<ItemStack> HELD_ITEM = EntityDataManager.createKey(EntityCarbuncle.class, DataSerializers.ITEMSTACK);
    public int backOff; // Used to stop inventory store/take spam when chests are full or empty.

    public EntityCarbuncle(EntityType<EntityCarbuncle> entityCarbuncleEntityType, World world) {
        super(entityCarbuncleEntityType, world);
    }

    @Override
    public void tick() {
        super.tick();
        if(world.isRemote)
            return;

        if(this.backOff > 0)
            this.backOff--;

        if(this.getHeldStack().isEmpty()){

            for(ItemEntity itementity : this.world.getEntitiesWithinAABB(ItemEntity.class, this.getBoundingBox().grow(1.0D, 0.0D, 1.0D))) {
                if (!itementity.removed && !itementity.getItem().isEmpty() && !itementity.cannotPickup()) {
                    this.updateEquipmentIfNeeded(itementity);
                }
            }
        }
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
        this.goalSelector.addGoal(5, new FindItem());
        this.goalSelector.addGoal(8, new StoreItemGoal());
        this.goalSelector.addGoal(8, new TakeItemGoal());
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(6, new LookAtGoal(this, MobEntity.class, 8.0F));
//        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
    }

    public class FindItem extends Goal{
        private final Predicate<ItemEntity> TRUSTED_TARGET_SELECTOR = (p_213489_0_) -> {
            return !p_213489_0_.cannotPickup() && p_213489_0_.isAlive();
        };

        public FindItem(){
            this.setMutexFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean shouldExecute() {
            List<ItemEntity> list = EntityCarbuncle.this.world.getEntitiesWithinAABB(ItemEntity.class, EntityCarbuncle.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), TRUSTED_TARGET_SELECTOR);
            return !list.isEmpty() && EntityCarbuncle.this.getHeldStack().isEmpty();
        }

        @Override
        public void startExecuting() {
            super.startExecuting();
            List<ItemEntity> list = EntityCarbuncle.this.world.getEntitiesWithinAABB(ItemEntity.class, EntityCarbuncle.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), TRUSTED_TARGET_SELECTOR);
            ItemStack itemstack = EntityCarbuncle.this.getHeldStack();

            if (itemstack.isEmpty() && !list.isEmpty()) {
                EntityCarbuncle.this.pathToTarget(list.get(0), 1.2f);
            }
        }

        @Override
        public void tick() {
            super.tick();

            List<ItemEntity> list = EntityCarbuncle.this.world.getEntitiesWithinAABB(ItemEntity.class, EntityCarbuncle.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), TRUSTED_TARGET_SELECTOR);
            ItemStack itemstack = EntityCarbuncle.this.getHeldStack();
            if (itemstack.isEmpty() && !list.isEmpty()) {
                EntityCarbuncle.this.pathToTarget(list.get(0), 1.2f);
            }
        }
    }

    public void pathToTarget(Entity entity, double speed){
        Path path = this.getNavigator().getPathToEntity(entity, 0);
        this.getNavigator().setPath(path, speed);
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
                    EntityCarbuncle.this.setHeldStack(left);
//                    EntityCarbuncle.this.world.playSound(null, EntityCarbuncle.this.getPosX(), EntityCarbuncle.this.getPosY(), EntityCarbuncle.this.getPosZ(), SoundEvents.ENTITY_ITEM_PICKUP, EntityCarbuncle.this.getSoundCategory(),1.0F, 1.0F);
                    EntityCarbuncle.this.backOff = 20;
                    return;
                }
            }

            if(EntityCarbuncle.this.toPos != null && !EntityCarbuncle.this.getHeldStack().isEmpty()) {
                EntityCarbuncle.this.getNavigator().tryMoveToXYZ(EntityCarbuncle.this.toPos.getX(), EntityCarbuncle.this.toPos.getY(), EntityCarbuncle.this.toPos.getZ(), 1.2D);
            }
        }

        @Override
        public boolean shouldContinueExecuting() {
            return EntityCarbuncle.this.getHeldStack() != null && !EntityCarbuncle.this.getHeldStack().isEmpty() && EntityCarbuncle.this.backOff == 0;
        }

        @Override
        public boolean shouldExecute() {
            return EntityCarbuncle.this.getHeldStack() != null && !EntityCarbuncle.this.getHeldStack().isEmpty() && EntityCarbuncle.this.backOff == 0;
        }
    }


    public class TakeItemGoal extends Goal{

        public TakeItemGoal(){
            this.setMutexFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public void startExecuting() {
            super.startExecuting();
            if(EntityCarbuncle.this.fromPos != null && EntityCarbuncle.this.getHeldStack().isEmpty())
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
            return EntityCarbuncle.this.getHeldStack() != null && EntityCarbuncle.this.getHeldStack().isEmpty() && EntityCarbuncle.this.backOff == 0;
        }

        @Override
        public boolean shouldExecute() {
            return EntityCarbuncle.this.getHeldStack() != null && EntityCarbuncle.this.getHeldStack().isEmpty() && EntityCarbuncle.this.backOff == 0;
        }
    }


    @Override
    protected boolean processInteract(PlayerEntity player, Hand hand) {
        System.out.println(this.getHeldStack());
        System.out.println(this.toPos);
        System.out.println(this.fromPos);
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
    }
}
