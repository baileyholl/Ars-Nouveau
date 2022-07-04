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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.EnumSet;

public class TakeItemGoal extends ExtendedRangeGoal {
    Starbuncle starbuncle;
    BlockPos takePos;
    boolean unreachable;
    StarbyTransportBehavior behavior;

    public TakeItemGoal(Starbuncle starbuncle, StarbyTransportBehavior transportBehavior) {
        super(25);
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.starbuncle = starbuncle;
        this.behavior = transportBehavior;
    }

    @Override
    public void stop() {
        super.stop();
        takePos = null;
        unreachable = false;
        startDistance = 0.0;
        starbuncle.goalState = Starbuncle.StarbuncleGoalState.NONE;
    }

    @Override
    public void start() {
        super.start();
        takePos = behavior.getValidTakePos();
        unreachable = false;
        if (starbuncle.isTamed() && takePos != null && starbuncle.getHeldStack().isEmpty()) {
            startDistance = BlockUtil.distanceFrom(starbuncle.position, takePos);
            setPath(takePos.getX(), takePos.getY(), takePos.getZ(), 1.2D);
        }
        if (takePos == null) {
            starbuncle.setBackOff(60 + starbuncle.level.random.nextInt(60));
        }
        starbuncle.goalState = Starbuncle.StarbuncleGoalState.TAKING_ITEM;
    }


    public void getItem() {
        Level world = starbuncle.level;
        if (world.getBlockEntity(takePos) == null)
            return;
        IItemHandler iItemHandler = world.getBlockEntity(takePos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if (iItemHandler == null)
            return;
        for (int j = 0; j < iItemHandler.getSlots(); j++) {
            if (!iItemHandler.getStackInSlot(j).isEmpty()) {
                int count = behavior.getMaxTake(iItemHandler.getStackInSlot(j));
                if (count <= 0)
                    continue;
                behavior.getValidStorePos(iItemHandler.getStackInSlot(j));

                starbuncle.setHeldStack(iItemHandler.extractItem(j, count, false));

                starbuncle.level.playSound(null, starbuncle.getX(), starbuncle.getY(), starbuncle.getZ(),
                        SoundEvents.ITEM_PICKUP, starbuncle.getSoundSource(), 1.0F, 1.0F);

                if (world instanceof ServerLevel serverLevel) {
                    // Potential bug with OpenJDK causing irreproducible noClassDef errors
                    try {
                        OpenChestEvent event = new OpenChestEvent(serverLevel, takePos, 20);
                        event.open();
                        EventQueue.getServerInstance().addEvent(event);
                    } catch (Throwable ignored) {
                    }
                }
                break;
            }
        }
    }

    public void setPath(double x, double y, double z, double speedIn) {
        starbuncle.getNavigation().tryMoveToBlockPos(new BlockPos(x, y, z), 1.3);
        if (starbuncle.getNavigation().getPath() != null && !starbuncle.getNavigation().getPath().canReach()) {
            unreachable = true;
        }
    }

    @Override
    public void tick() {
        super.tick();
        // Retry the valid position
        if (this.ticksRunning % 100 == 0 && !behavior.isPositionValidTake(takePos)) {
            takePos = null;
            return;
        }
        if (starbuncle.getHeldStack().isEmpty() && takePos != null && BlockUtil.distanceFrom(starbuncle.position(), takePos) <= 2d + this.extendedRange) {
            Level world = starbuncle.level;
            BlockEntity tileEntity = world.getBlockEntity(takePos);
            if (tileEntity == null)
                return;
            IItemHandler iItemHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
            if (iItemHandler != null) {
                getItem();
                return;
            }
        }

        if (takePos != null && starbuncle.getHeldStack().isEmpty()) {
            setPath(takePos.getX(), takePos.getY(), takePos.getZ(), 1.3D);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !unreachable && starbuncle.getHeldStack() != null && starbuncle.getHeldStack().isEmpty() && starbuncle.getBackOff() == 0 && starbuncle.isTamed() && takePos != null;
    }

    @Override
    public boolean canUse() {
        return starbuncle.getHeldStack() != null && starbuncle.getHeldStack().isEmpty() && starbuncle.getBackOff() == 0;
    }

    @Override
    public boolean isInterruptable() {
        return super.isInterruptable();
    }
}
