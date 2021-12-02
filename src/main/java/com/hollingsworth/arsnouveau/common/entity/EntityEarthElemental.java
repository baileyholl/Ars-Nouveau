package com.hollingsworth.arsnouveau.common.entity;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


public class EntityEarthElemental extends PathfinderMob  {

    protected EntityEarthElemental(EntityType<? extends PathfinderMob> type, Level worldIn) {
        super(type, worldIn);
    }
    public EntityEarthElemental(Level world){
        this(ModEntities.ENTITY_EARTH_ELEMENTAL_TYPE,world);
    }



    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

    }

//    @Override
//    protected boolean processInteract(PlayerEntity player, Hand hand) {
////        System.out.println(this.getHeldStack());
//        System.out.println(this.getEntityId());
//        if(player.getEntityWorld().isRemote)
//            System.out.println(this.manager.get("idleController"));
//        return super.processInteract(player, hand);
//    }

    @Override
    public void tick() {
        super.tick();
        if(getHeldStack().isEmpty() && !level.isClientSide){
            for(ItemEntity itementity : this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(1.0D, 0.0D, 1.0D))) {
                if (!itementity.removed && !itementity.getItem().isEmpty() && !itementity.hasPickUpDelay()) {
                    this.pickUpItem(itementity);
                    System.out.println("sending packet");
//                    Networking.sendToNearby(world, this, new PacketEntityAnimationSync(this.getEntityId(), "smeltController", "smelting"));
//                    EventQueue.getInstance().addEvent(new ProcessOreEvent(this, 20 * 20));
                }
            }
        }
    }

    /**
     * Handler for
     */
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 45) {
            ItemStack itemstack = this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (!itemstack.isEmpty()) {
                for(int i = 0; i < 8; ++i) {
//                    Vec3d vec3d = (new Vec3d(((double)this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D)).rotatePitch(-this.rotationPitch * ((float)Math.PI / 180F)).rotateYaw(-this.rotationYaw * ((float)Math.PI / 180F));
//                    this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, itemstack), this.getPosX() + this.getLookVec().x / 2.0D, this.getPosY(), this.getPosZ() + this.getLookVec().z / 2.0D, vec3d.x, vec3d.y + 0.05D, vec3d.z);
                }
            }
        } else {
            super.handleEntityEvent(id);
        }

    }

    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        if(this.getHeldStack().isEmpty()){
            setHeldStack(itemEntity.getItem());
            itemEntity.remove();
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_PICKUP, this.getSoundSource(),1.0F, 1.0F);
        }
    }
    public void setHeldStack(ItemStack stack){
//        this.dataManager.set(HELD_ITEM,stack);
        this.setItemSlot(EquipmentSlot.MAINHAND, stack);
    }

    public ItemStack getHeldStack(){
        return this.getMainHandItem();
//        return this.dataManager.get(HELD_ITEM);
    }


    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if(getHeldStack() != null) {
            CompoundTag itemTag = new CompoundTag();
            getHeldStack().save(itemTag);
            tag.put("held", itemTag);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if(tag.contains("held"))
            setHeldStack(ItemStack.of((CompoundTag)tag.get("held")));
    }
}
