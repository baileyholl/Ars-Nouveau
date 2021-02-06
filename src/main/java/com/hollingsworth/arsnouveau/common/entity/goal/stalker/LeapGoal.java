package com.hollingsworth.arsnouveau.common.entity.goal.stalker;

import com.hollingsworth.arsnouveau.common.entity.WildenStalker;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class LeapGoal extends Goal {
    WildenStalker stalker;

    public LeapGoal(WildenStalker stalker){
        this.stalker = stalker;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public void startExecuting() {
        stalker.setVelocity(0, 2.0, 0);
        stalker.setLeapCooldown(400);
        stalker.setFlying(true);
    }

    @Override
    public boolean shouldExecute() {
        return stalker.getAttackTarget() != null && !stalker.isFlying() && stalker.getLeapCooldown() == 0;
    }
}
