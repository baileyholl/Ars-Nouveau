package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.debug.DebugEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class GoToBedGoal extends Goal {

    boolean unreachable;
    public Starbuncle starbuncle;
    BlockPos bedPos;
    public StarbyBehavior behavior;
    public int ticksRunning;

    public GoToBedGoal(Starbuncle starbuncle, StarbyBehavior behavior) {
        this.starbuncle = starbuncle;
        this.behavior = behavior;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canContinueToUse() {
        if (ticksRunning >= 20 * 15) {
            starbuncle.addGoalDebug(this, new DebugEvent("BedTimeout", "Took too long to find bed"));
            starbuncle.goalState = Starbuncle.StarbuncleGoalState.NONE;
            return false;
        }

        if (bedPos == null || starbuncle.data.bedPos == null)
            return false;

        boolean bedValid = true;
        boolean isOnBed = false;
        // Time defer these checks otherwise we will destroy TPS with blockstate lookups.
        if (starbuncle.level.getGameTime() % 10 == 0) {
            bedValid = isBedValid();
            isOnBed = isOnBed();
        }

        return !unreachable && bedValid && !isOnBed;
    }

    @Override
    public void start() {
        super.start();
        unreachable = false;
        starbuncle.getNavigation().stop();
        starbuncle.goalState = Starbuncle.StarbuncleGoalState.RESTING;
        ticksRunning = 0;
        bedPos = starbuncle.data.bedPos;
    }

    @Override
    public void stop() {
        super.stop();
        ticksRunning = 0;
    }

    @Override
    public void tick() {
        super.tick();
        ticksRunning++;
        setPath(bedPos.getX(), bedPos.getY() + 1.0, bedPos.getZ(), 1.3);
        starbuncle.addGoalDebug(this, new DebugEvent("PathToBed", "Pathing to " + bedPos.getX() + " " + bedPos.getY() + " " + bedPos.getZ()));
    }


    public void setPath(double x, double y, double z, double speedIn) {
        starbuncle.getNavigation().tryMoveToBlockPos(BlockPos.containing(x, y, z), 1.3);
        if (starbuncle.getNavigation().getPath() != null && !starbuncle.getNavigation().getPath().canReach()) {
            starbuncle.addGoalDebug(this, new DebugEvent("BedUnreachable", "Unreachable"));
            unreachable = true;
        }
    }

    @Override
    public boolean canUse() {
        if (starbuncle.level.random.nextInt(2) == 0) {
            return false;
        }
        bedPos = starbuncle.data.bedPos;
        if (starbuncle.getBedBackoff() > 0
                || starbuncle.goalState != Starbuncle.StarbuncleGoalState.NONE
                || bedPos == null
                || !behavior.canGoToBed()) {
            starbuncle.addGoalDebug(this, new DebugEvent("CannotSleep", "Bed not valid" + " backoff: " + starbuncle.getBedBackoff()));
            return false;
        }
        boolean canRun = isBedValid() && !isOnBed();
        if (!canRun) {
            starbuncle.setBedBackoff(20 * 3);
            starbuncle.addGoalDebug(this, new DebugEvent("InvalidBed", "Bed position invalid"));
        }
        return canRun;
    }

    public boolean isBedValid() {
        return starbuncle.level.isLoaded(bedPos) && starbuncle.level.getBlockState(new BlockPos(bedPos)).is(BlockTagProvider.SUMMON_SLEEPABLE);
    }

    public boolean isOnBed() {
        return starbuncle.level.getBlockState(BlockPos.containing(starbuncle.position)).is(BlockTagProvider.SUMMON_SLEEPABLE);
    }

    @Override
    public boolean isInterruptable() {
        return true;
    }
}
