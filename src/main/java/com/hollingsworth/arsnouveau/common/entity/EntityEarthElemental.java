package com.hollingsworth.arsnouveau.common.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


public class EntityEarthElemental extends CreatureEntity  {

    protected EntityEarthElemental(EntityType<? extends CreatureEntity> type, World worldIn) {
        super(type, worldIn);
    }
    public EntityEarthElemental(World world){
        this(ModEntities.ENTITY_EARTH_ELEMENTAL_TYPE,world);
    }



    @Override
    protected void registerData() {
        super.registerData();

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
        if(getHeldStack().isEmpty() && !world.isRemote){
            for(ItemEntity itementity : this.world.getEntitiesWithinAABB(ItemEntity.class, this.getBoundingBox().grow(1.0D, 0.0D, 1.0D))) {
                if (!itementity.removed && !itementity.getItem().isEmpty() && !itementity.cannotPickup()) {
                    this.updateEquipmentIfNeeded(itementity);
                    System.out.println("sending packet");
//                    Networking.sendToNearby(world, this, new PacketEntityAnimationSync(this.getEntityId(), "smeltController", "smelting"));
//                    EventQueue.getInstance().addEvent(new ProcessOreEvent(this, 20 * 20));
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
//                    Vec3d vec3d = (new Vec3d(((double)this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D)).rotatePitch(-this.rotationPitch * ((float)Math.PI / 180F)).rotateYaw(-this.rotationYaw * ((float)Math.PI / 180F));
//                    this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, itemstack), this.getPosX() + this.getLookVec().x / 2.0D, this.getPosY(), this.getPosZ() + this.getLookVec().z / 2.0D, vec3d.x, vec3d.y + 0.05D, vec3d.z);
                }
            }
        } else {
            super.handleStatusUpdate(id);
        }

    }

    @Override
    protected void updateEquipmentIfNeeded(ItemEntity itemEntity) {
        if(this.getHeldStack().isEmpty()){
            setHeldStack(itemEntity.getItem());
            itemEntity.remove();
            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_ITEM_PICKUP, this.getSoundCategory(),1.0F, 1.0F);
        }
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
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        if(tag.contains("held"))
            setHeldStack(ItemStack.read((CompoundNBT)tag.get("held")));
    }
}
