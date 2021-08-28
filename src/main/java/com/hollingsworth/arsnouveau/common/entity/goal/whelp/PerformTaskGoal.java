package com.hollingsworth.arsnouveau.common.entity.goal.whelp;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityWhelp;
import com.hollingsworth.arsnouveau.common.entity.goal.ExtendedRangeGoal;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class PerformTaskGoal extends ExtendedRangeGoal {

    EntityWhelp wyrm;
    BlockPos taskLoc;
    int timePerformingTask;

    public PerformTaskGoal(EntityWhelp wyrm) {
        super(10);
        this.wyrm = wyrm;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void stop() {
        super.stop();
        timePerformingTask = 0;
    }

    @Override
    public void start() {
        super.start();
        taskLoc = this.wyrm.getTaskLoc();
        timePerformingTask = 0;
        if (taskLoc != null){
            this.startDistance = BlockUtil.distanceFrom(wyrm.position, taskLoc);
        }
    }

    @Override
    public void tick() {
        super.tick();
        timePerformingTask++;
        if (taskLoc == null)
            return;
        if (BlockUtil.distanceFrom(wyrm.position, taskLoc.above(2)) <= 2 + this.extendedRange) {
            wyrm.castSpell(taskLoc);
            timePerformingTask = 0;
        } else{
            this.wyrm.getNavigation().moveTo(this.wyrm.getNavigation().createPath(taskLoc.above(2), 1), 1.2f);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return wyrm.ticksSinceLastSpell > 60 && this.taskLoc != null && timePerformingTask < 300;
    }

    @Override
    public boolean canUse() {
        return wyrm.canPerformAnotherTask() && wyrm.enoughManaForTask();
    }
}
