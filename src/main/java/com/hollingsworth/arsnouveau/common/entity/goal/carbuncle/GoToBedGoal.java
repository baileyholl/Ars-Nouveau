package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.common.block.SummonBed;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Block;

public class GoToBedGoal extends Goal {
    int ticksSleeping;
    boolean unreachable;
    public Starbuncle starbuncle;

    public GoToBedGoal(Starbuncle starbuncle) {
        this.starbuncle = starbuncle;
    }

    @Override
    public boolean canContinueToUse() {
        return !unreachable && canUse();
    }

    @Override
    public void start() {
        super.start();
        unreachable = false;
        starbuncle.getNavigation().stop();
        starbuncle.goalState = Starbuncle.StarbuncleGoalState.RESTING;
    }

    @Override
    public void stop() {
        super.stop();
        starbuncle.goalState = Starbuncle.StarbuncleGoalState.NONE;
    }

    @Override
    public void tick() {
        super.tick();
        setPath(starbuncle.bedPos.getX(), starbuncle.bedPos.getY() + 1, starbuncle.bedPos.getZ(), 1.3);

    }


    public void setPath(double x, double y, double z, double speedIn){
        starbuncle.getNavigation().tryMoveToBlockPos(new BlockPos(x, y, z), 1.3);
        if(starbuncle.getNavigation().getPath() != null && !starbuncle.getNavigation().getPath().canReach()) {
            unreachable = true;
        }
    }

    @Override
    public boolean canUse() {
        if(starbuncle.goalState != Starbuncle.StarbuncleGoalState.NONE || starbuncle.bedPos == null || starbuncle.getValidTakePos() != null) {
            return false;
        }
        if(!starbuncle.getHeldStack().isEmpty() && starbuncle.getValidStorePos(starbuncle.getHeldStack()) != null) {
            return false;
        }
        Block onBlock = starbuncle.level.getBlockState(new BlockPos(starbuncle.bedPos)).getBlock();
        if(!(onBlock instanceof SummonBed)) {
            return false;
        }
        return !(starbuncle.level.getBlockState(new BlockPos(starbuncle.position)).getBlock() instanceof SummonBed);
    }

    @Override
    public boolean isInterruptable() {
        return true;
    }
}
