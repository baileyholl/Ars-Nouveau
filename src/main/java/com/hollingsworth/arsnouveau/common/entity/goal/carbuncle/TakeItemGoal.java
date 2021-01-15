package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.hollingsworth.arsnouveau.common.event.OpenChestEvent;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.EnumSet;

public class TakeItemGoal extends Goal {
    EntityCarbuncle carbuncle;
    BlockPos takePos;
    public TakeItemGoal(EntityCarbuncle carbuncle){
      //  super(carbuncle::getPosition, 3, carbuncle::setStuck);
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
        this.carbuncle = carbuncle;
    }


    @Override
    public void resetTask() {
        super.resetTask();
        takePos = null;
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        takePos = carbuncle.getValidTakePos();
        if(carbuncle.isTamed() && takePos != null && carbuncle.getHeldStack().isEmpty())
            setPath(takePos.getX(), takePos.getY(), takePos.getZ(), 1.2D);
    }


    public void getItem(){
        World world = carbuncle.world;
        IItemHandler iItemHandler = world.getTileEntity(takePos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if(iItemHandler == null)
            return;
        for(int j = 0; j < iItemHandler.getSlots(); j++){
            if(!iItemHandler.getStackInSlot(j).isEmpty() && carbuncle.isValidItem( iItemHandler.getStackInSlot(j))){

                carbuncle.setHeldStack(iItemHandler.extractItem(j, 64, false));

                carbuncle.world.playSound(null, carbuncle.getPosX(),carbuncle.getPosY(), carbuncle.getPosZ(),
                        SoundEvents.ENTITY_ITEM_PICKUP, carbuncle.getSoundCategory(),1.0F, 1.0F);

                if(world instanceof ServerWorld){
                    OpenChestEvent event = new OpenChestEvent(FakePlayerFactory.getMinecraft((ServerWorld) world), takePos, 20);
                    event.open();
                    EventQueue.getInstance().addEvent(event);
                }
                break;
            }
        }
    }
    public void setPath(double x, double y, double z, double speedIn){
        carbuncle.getNavigator().setPath( carbuncle.getNavigator().getPathToPos(x+0.5, y+1, z+0.5, 1), speedIn);
    }

    @Override
    public void tick() {
        if(carbuncle.getHeldStack().isEmpty() && takePos != null && BlockUtil.distanceFrom(carbuncle.getPosition(), takePos) < 2d){
            World world = carbuncle.world;
            TileEntity tileEntity = world.getTileEntity(takePos);
            if(tileEntity == null)
                return;
            IItemHandler iItemHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
            if(iItemHandler != null){
                getItem();
                return;
            }
        }

        if(takePos != null && carbuncle.getHeldStack().isEmpty()) {
            setPath(takePos.getX(), takePos.getY(), takePos.getZ(), 1.2D);
            super.tick();
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !carbuncle.isStuck && carbuncle.getHeldStack() != null && carbuncle.getHeldStack().isEmpty() && carbuncle.backOff == 0 && carbuncle.isTamed() && takePos != null;
    }

    @Override
    public boolean shouldExecute() {
        return !carbuncle.isStuck && carbuncle.getHeldStack() != null &&carbuncle.getHeldStack().isEmpty() && carbuncle.backOff == 0 && carbuncle.isTamed();
    }
}
