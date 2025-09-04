package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.debug.DebugEvent;
import com.hollingsworth.arsnouveau.common.entity.goal.ExtendedRangeGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Supplier;

public abstract class GoToPosGoal<T extends StarbyBehavior> extends ExtendedRangeGoal {
    public Starbuncle starbuncle;
    public T behavior;
    Supplier<Boolean> canUse;
    Supplier<Boolean> canContinue;
    public boolean isDone;
    public BlockPos targetPos;

    public GoToPosGoal(Starbuncle starbuncle, T behavior, Supplier<Boolean> canUse, Supplier<Boolean> canContinue) {
        super(30);
        this.starbuncle = starbuncle;
        this.behavior = behavior;
        this.canUse = canUse;
        this.canContinue = canContinue;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public GoToPosGoal(Starbuncle starbuncle, T behavior, Supplier<Boolean> canUse) {
        this(starbuncle, behavior, canUse, canUse);
    }

    @Override
    public void stop() {
        super.stop();
        isDone = false;
        targetPos = null;
        starbuncle.goalState = Starbuncle.StarbuncleGoalState.NONE;
    }

    @Override
    public void start() {
        super.start();
        isDone = false;
        this.targetPos = getDestination();
        if (targetPos == null) {
            starbuncle.setBackOff(60 + starbuncle.level.random.nextInt(60));
            return;
        }
        starbuncle.addGoalDebug(this, new DebugEvent("StartedGoal", "Started goal "));

    }

    @Override
    public void tick() {
        super.tick();
        if (targetPos == null)
            return;
        if (this.ticksRunning % 100 == 0 && !isDestinationStillValid(targetPos)) {
            starbuncle.addDebugEvent(new DebugEvent("became_invalid", "Invalid position " + targetPos.toString()));
            isDone = true;
            return;
        }

        if (BlockUtil.distanceFrom(starbuncle.position().add(0, 0.5, 0), new Vec3(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5)) <= 2.5D + this.extendedRange && isDestinationStillValid(targetPos)) {
            isDone = onDestinationReached();
            return;
        }

        if (targetPos != null) {
            setPath(targetPos);
        }
    }

    @Override
    public boolean canUse() {
        return canUse.get() && starbuncle.getBackOff() <= 0;
    }

    @Override
    public boolean canContinueToUse() {
        return targetPos != null && !isDone && canContinue.get();
    }

    public void setPath(BlockPos pos) {
        starbuncle.getNavigation().tryMoveToBlockPos(pos, 1.3);
        starbuncle.addGoalDebug(this, new DebugEvent("path_set", "path set to " + targetPos.toString()));
        if (starbuncle.getNavigation().getPath() != null && !starbuncle.getNavigation().getPath().canReach()) {
            isDone = true;
            starbuncle.addGoalDebug(this, new DebugEvent("unreachable", targetPos.toString()));
        }
    }

    public abstract @Nullable BlockPos getDestination();

    /**
     * Returns whether we are done and can end the goal.
     */
    public abstract boolean onDestinationReached();

    public boolean isDestinationStillValid(BlockPos pos) {
        return true;
    }

}
