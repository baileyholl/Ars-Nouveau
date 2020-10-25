package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.hollingsworth.arsnouveau.common.event.OpenChestEvent;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.EnumSet;
import java.util.stream.IntStream;

public class StoreItemGoal extends Goal {

    private final EntityCarbuncle entityCarbuncle;

    public StoreItemGoal(EntityCarbuncle entityCarbuncle) {
        this.entityCarbuncle = entityCarbuncle;

        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        if (entityCarbuncle.toPos != null && !entityCarbuncle.getHeldStack().isEmpty())
            entityCarbuncle.getNavigator().tryMoveToXYZ(entityCarbuncle.toPos.getX(), entityCarbuncle.toPos.getY(), entityCarbuncle.toPos.getZ(), 1.2D);
    }

    private IntStream func_213972_a(IInventory p_213972_0_, Direction p_213972_1_) {
        return p_213972_0_ instanceof ISidedInventory ? IntStream.of(((ISidedInventory) p_213972_0_).getSlotsForFace(p_213972_1_)) : IntStream.range(0, p_213972_0_.getSizeInventory());
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
        if (!entityCarbuncle.getHeldStack().isEmpty() && entityCarbuncle.toPos != null && BlockUtil.distanceFrom(entityCarbuncle.getPosition(), entityCarbuncle.toPos) < 1.25D) {
            World world = entityCarbuncle.world;
            if (world.getTileEntity(entityCarbuncle.toPos) instanceof IInventory) {
                ItemStack oldStack = new ItemStack(entityCarbuncle.getHeldStack().getItem(), entityCarbuncle.getHeldStack().getCount());

                IInventory i = (IInventory) world.getTileEntity(entityCarbuncle.toPos);
                ItemStack left = HopperTileEntity.putStackInInventoryAllSlots(null, i, entityCarbuncle.getHeldStack(), null);
                if (left.equals(oldStack)) {
                    return;
                }
                if (world instanceof ServerWorld) {
                    OpenChestEvent event = new OpenChestEvent(FakePlayerFactory.getMinecraft((ServerWorld) world), entityCarbuncle.toPos, 20);
                    event.open();
                    EventQueue.getInstance().addEvent(event);
                }
                entityCarbuncle.setHeldStack(left);
//                    EntityCarbuncle.this.world.playSound(null, EntityCarbuncle.this.getPosX(), EntityCarbuncle.this.getPosY(), EntityCarbuncle.this.getPosZ(), SoundEvents.ENTITY_ITEM_PICKUP, EntityCarbuncle.this.getSoundCategory(),1.0F, 1.0F);
                entityCarbuncle.backOff = 20;

                entityCarbuncle.getDataManager().set(EntityCarbuncle.HOP, false);
                return;
            }
        }

        if (entityCarbuncle.toPos != null && !entityCarbuncle.getHeldStack().isEmpty()) {
            entityCarbuncle.getNavigator().tryMoveToXYZ(entityCarbuncle.toPos.getX(), entityCarbuncle.toPos.getY(), entityCarbuncle.toPos.getZ(), 1.2D);
            entityCarbuncle.getDataManager().set(EntityCarbuncle.HOP, true);
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        return entityCarbuncle.isTamed() && entityCarbuncle.getHeldStack() != null && !entityCarbuncle.getHeldStack().isEmpty() && entityCarbuncle.backOff == 0;
    }

    @Override
    public boolean shouldExecute() {
        return entityCarbuncle.isTamed() && entityCarbuncle.getHeldStack() != null && !entityCarbuncle.getHeldStack().isEmpty() && entityCarbuncle.backOff == 0;
    }
}
