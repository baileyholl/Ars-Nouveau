package com.hollingsworth.arsnouveau.common.entity.goal.whelp;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityWhelp;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class PerformTaskGoal extends Goal {

    EntityWhelp kobold;
    BlockPos taskLoc;
    int timePerformingTask;

    public PerformTaskGoal(EntityWhelp kobold) {
        this.kobold = kobold;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }


    @Override
    public void resetTask() {
        super.resetTask();
        timePerformingTask = 0;
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        taskLoc = this.kobold.getTaskLoc();
        timePerformingTask = 0;
        if (this.kobold.getNavigator() != null && taskLoc != null)
            this.kobold.getNavigator().setPath(this.kobold.getNavigator().getPathToPos(taskLoc, 1), 1.2f);
    }

    @Override
    public void tick() {
        super.tick();

        timePerformingTask++;
        if (kobold == null || taskLoc == null)
            return;

        if (BlockUtil.distanceFrom(kobold.getPosition(), taskLoc) <= 2) {
            kobold.castSpell(taskLoc);
            kobold.getNavigator().clearPath();
            timePerformingTask = 0;
        } else if (kobold.getNavigator() != null) {
            this.kobold.getNavigator().setPath(this.kobold.getNavigator().getPathToPos(taskLoc.up(2), 0), 1.2f);
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        return kobold.ticksSinceLastSpell > 60 && this.taskLoc != null && timePerformingTask < 300;
    }

    @Override
    public boolean shouldExecute() {
        return kobold.canPerformAnotherTask() && kobold.enoughManaForTask();
    }
}
