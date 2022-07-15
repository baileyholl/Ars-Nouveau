package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.common.block.SummonBed;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Block;

public class GoToBedGoal extends Goal {

    boolean unreachable;
    public Starbuncle starbuncle;
    BlockPos bedPos;
    public StarbyBehavior behavior;

    public GoToBedGoal(Starbuncle starbuncle, StarbyBehavior behavior) {
        this.starbuncle = starbuncle;
        this.behavior = behavior;
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
        bedPos = starbuncle.data.bedPos;
    }

    @Override
    public void stop() {
        super.stop();
        starbuncle.goalState = Starbuncle.StarbuncleGoalState.NONE;
    }

    @Override
    public void tick() {
        super.tick();
        setPath(bedPos.getX(), bedPos.getY() + 1.0, bedPos.getZ(), 1.3);

    }


    public void setPath(double x, double y, double z, double speedIn) {
        starbuncle.getNavigation().tryMoveToBlockPos(new BlockPos(x, y, z), 1.3);
        if (starbuncle.getNavigation().getPath() != null && !starbuncle.getNavigation().getPath().canReach()) {
            unreachable = true;
        }
    }

    @Override
    public boolean canUse() {
        if (starbuncle.goalState != Starbuncle.StarbuncleGoalState.NONE || bedPos == null || !behavior.canGoToBed()) {
            return false;
        }
        Block onBlock = starbuncle.level.getBlockState(new BlockPos(bedPos)).getBlock();
        if (!(onBlock instanceof SummonBed)) {
            return false;
        }
        return !(starbuncle.level.getBlockState(new BlockPos(starbuncle.position)).getBlock() instanceof SummonBed);
    }

    @Override
    public boolean isInterruptable() {
        return true;
    }
}
