package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.hollingsworth.arsnouveau.common.event.OpenChestEvent;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.EnumSet;

public class StoreItemGoal extends Goal {

    private final EntityCarbuncle entityCarbuncle;

    public StoreItemGoal(EntityCarbuncle entityCarbuncle) {
        //super(entityCarbuncle::getPosition, 3, entityCarbuncle::setStuck);
        this.entityCarbuncle = entityCarbuncle;

        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        if (entityCarbuncle.getToPos() != null && !entityCarbuncle.getHeldStack().isEmpty()) {
            Path path = entityCarbuncle.getNavigator().getPathToPos(entityCarbuncle.getToPos(), 0);
            entityCarbuncle.getNavigator().setPath(path, 1.2D);
            //entityCarbuncle.getNavigator().tryMoveToXYZ(entityCarbuncle.toPos.getX(), entityCarbuncle.toPos.getY(), entityCarbuncle.toPos.getZ(), 1.2D);
        }
    }

    @Override
    public void tick() {
        if (!entityCarbuncle.getHeldStack().isEmpty() && entityCarbuncle.getToPos() != null && BlockUtil.distanceFrom(entityCarbuncle.getPosition(), entityCarbuncle.getToPos()) < 1.5D) {
            World world = entityCarbuncle.world;
            TileEntity tileEntity = world.getTileEntity(entityCarbuncle.getToPos());
            if(tileEntity == null)
                return;
            IItemHandler iItemHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
            if (iItemHandler != null) {
                ItemStack oldStack = new ItemStack(entityCarbuncle.getHeldStack().getItem(), entityCarbuncle.getHeldStack().getCount());

                ItemStack left = ItemHandlerHelper.insertItemStacked(iItemHandler, entityCarbuncle.getHeldStack(), false);
                if (left.equals(oldStack)) {
                    return;
                }
                if (world instanceof ServerWorld) {
                    OpenChestEvent event = new OpenChestEvent(FakePlayerFactory.getMinecraft((ServerWorld) world), entityCarbuncle.getToPos(), 20);
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

        if (entityCarbuncle.getToPos() != null && !entityCarbuncle.getHeldStack().isEmpty()) {
            setPath(entityCarbuncle.getToPos().getX(), entityCarbuncle.getToPos().getY(), entityCarbuncle.getToPos().getZ(), 1.2D);
            entityCarbuncle.getDataManager().set(EntityCarbuncle.HOP, true);
            super.tick();
        }

    }

    public void setPath(double x, double y, double z, double speedIn){
        entityCarbuncle.getNavigator().setPath( entityCarbuncle.getNavigator().getPathToPos(x+0.5, y+0.5, z+0.5, 0), speedIn);
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
