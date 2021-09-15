package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.ExtendedRangeGoal;
import com.hollingsworth.arsnouveau.common.event.OpenChestEvent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.EnumSet;

public class TakeItemGoal extends ExtendedRangeGoal {
    EntityCarbuncle carbuncle;
    BlockPos takePos;
    boolean unreachable;


    public TakeItemGoal(EntityCarbuncle carbuncle){
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
        World world = carbuncle.level;
        if(world.getBlockEntity(takePos) == null)
            return;
        IItemHandler iItemHandler = world.getBlockEntity(takePos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if(iItemHandler == null)
            return;
        for(int j = 0; j < iItemHandler.getSlots(); j++){
            if(!iItemHandler.getStackInSlot(j).isEmpty() && carbuncle.isValidItem( iItemHandler.getStackInSlot(j))){

                carbuncle.setHeldStack(iItemHandler.extractItem(j, 64, false));

                carbuncle.level.playSound(null, carbuncle.getX(),carbuncle.getY(), carbuncle.getZ(),
                        SoundEvents.ITEM_PICKUP, carbuncle.getSoundSource(),1.0F, 1.0F);

                if(world instanceof ServerWorld){
                    OpenChestEvent event = new OpenChestEvent(FakePlayerFactory.getMinecraft((ServerWorld) world), takePos, 20);
                    event.open();
                    EventQueue.getServerInstance().addEvent(event);
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
            World world = carbuncle.level;
            TileEntity tileEntity = world.getBlockEntity(takePos);
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
        return !unreachable && !carbuncle.isStuck && carbuncle.getHeldStack() != null && carbuncle.getHeldStack().isEmpty() && carbuncle.backOff == 0 && carbuncle.isTamed() && takePos != null;
    }

    @Override
    public boolean canUse() {
        return !carbuncle.isStuck && carbuncle.getHeldStack() != null && carbuncle.getHeldStack().isEmpty() && carbuncle.backOff == 0 && carbuncle.isTamed();
    }
}
