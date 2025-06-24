package com.hollingsworth.arsnouveau.common.entity.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;

import java.util.function.Supplier;

public class ConditionalLeapGoal extends LeapAtTargetGoal {
    public Supplier<Boolean> canUse;

    public ConditionalLeapGoal(Mob pMob, float pYd, Supplier<Boolean> canUse) {
        super(pMob, pYd);
        this.canUse = canUse;
    }

    @Override
    public boolean canUse() {
        return canUse.get() && super.canUse();
    }
}
