package com.hollingsworth.arsnouveau.common.entity.goal.whelp;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityWhelp;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class PerformTaskGoal extends Goal {

    EntityWhelp kobold;
    BlockPos taskLoc;
    int timePerformingTask;

    public PerformTaskGoal(EntityWhelp kobold) {
        this.kobold = kobold;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }


    @Override
    public void stop() {
        super.stop();
        timePerformingTask = 0;
    }

    @Override
    public void start() {
        super.start();
        taskLoc = this.kobold.getTaskLoc();
        timePerformingTask = 0;
        if (this.kobold.getNavigation() != null && taskLoc != null)
            this.kobold.getNavigation().moveTo(this.kobold.getNavigation().createPath(taskLoc, 1), 1.2f);
    }

    @Override
    public void tick() {
        super.tick();

        timePerformingTask++;
        if (kobold == null || taskLoc == null)
            return;

        if (BlockUtil.distanceFrom(kobold.blockPosition(), taskLoc) <= 2) {
            kobold.castSpell(taskLoc);
            kobold.getNavigation().stop();
            timePerformingTask = 0;
        } else if (kobold.getNavigation() != null) {
            this.kobold.getNavigation().moveTo(this.kobold.getNavigation().createPath(taskLoc.above(2), 0), 1.2f);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return kobold.ticksSinceLastSpell > 60 && this.taskLoc != null && timePerformingTask < 300;
    }

    @Override
    public boolean canUse() {
        return kobold.canPerformAnotherTask() && kobold.enoughManaForTask();
    }
}
