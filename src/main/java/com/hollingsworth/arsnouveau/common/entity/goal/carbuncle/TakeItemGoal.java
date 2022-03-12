package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.ExtendedRangeGoal;
import com.hollingsworth.arsnouveau.common.event.OpenChestEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.EnumSet;

public class TakeItemGoal extends ExtendedRangeGoal {
    Starbuncle carbuncle;
    BlockPos takePos;
    boolean unreachable;


    public TakeItemGoal(Starbuncle carbuncle){
        super(25);
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.carbuncle = carbuncle;
    }

    @Override
    public void stop() {
        super.stop();
        takePos = null;
        unreachable = false;
        startDistance = 0.0;
    }

    @Override
    public void start() {
        super.start();
        takePos = carbuncle.getValidTakePos();
        unreachable = false;
        if(carbuncle.isTamed() && takePos != null && carbuncle.getHeldStack().isEmpty()) {
            startDistance = BlockUtil.distanceFrom(carbuncle.position, takePos);
            setPath(takePos.getX(), takePos.getY(), takePos.getZ(), 1.2D);
        }
    }


    public void getItem(){
        Level world = carbuncle.level;
        if(world.getBlockEntity(takePos) == null)
            return;
        IItemHandler iItemHandler = world.getBlockEntity(takePos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if(iItemHandler == null)
            return;
        for(int j = 0; j < iItemHandler.getSlots(); j++){
            if(!iItemHandler.getStackInSlot(j).isEmpty()){
                int count = carbuncle.getMaxTake(iItemHandler.getStackInSlot(j));
                if(count <= 0)
                    continue;
                carbuncle.getValidStorePos(iItemHandler.getStackInSlot(j));

                carbuncle.setHeldStack(iItemHandler.extractItem(j, count, false));

                carbuncle.level.playSound(null, carbuncle.getX(),carbuncle.getY(), carbuncle.getZ(),
                        SoundEvents.ITEM_PICKUP, carbuncle.getSoundSource(),1.0F, 1.0F);

                if(world instanceof ServerLevel){
                    // Potential bug with OpenJDK causing irreproducible noClassDef errors
                    try {
                        OpenChestEvent event = new OpenChestEvent(FakePlayerFactory.getMinecraft((ServerLevel) world), takePos, 20);
                        event.open();
                        EventQueue.getServerInstance().addEvent(event);
                    }catch (Throwable ignored){}
                }
                break;
            }
        }
    }

    public void setPath(double x, double y, double z, double speedIn){
        carbuncle.getNavigation().tryMoveToBlockPos(new BlockPos(x, y, z), 1.3);
        if(carbuncle.getNavigation().getPath() != null && !carbuncle.getNavigation().getPath().canReach()) {
            unreachable = true;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(carbuncle.getHeldStack().isEmpty() && takePos != null && BlockUtil.distanceFrom(carbuncle.position(), takePos) <= 2d + this.extendedRange){
            Level world = carbuncle.level;
            BlockEntity tileEntity = world.getBlockEntity(takePos);
            if(tileEntity == null)
                return;
            IItemHandler iItemHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
            if(iItemHandler != null){
                getItem();
                return;
            }
        }

        if(takePos != null && carbuncle.getHeldStack().isEmpty()) {
            setPath(takePos.getX(), takePos.getY(), takePos.getZ(), 1.3D);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !unreachable && !carbuncle.isStuck && carbuncle.getHeldStack() != null && carbuncle.getHeldStack().isEmpty() && carbuncle.getBackOff() == 0 && carbuncle.isTamed() && takePos != null;
    }

    @Override
    public boolean canUse() {
        return !carbuncle.isStuck && carbuncle.getHeldStack() != null && carbuncle.getHeldStack().isEmpty() && carbuncle.getBackOff() == 0 && carbuncle.isTamed();
    }
}
