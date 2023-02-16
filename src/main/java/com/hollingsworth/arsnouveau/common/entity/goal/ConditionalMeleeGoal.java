package com.hollingsworth.arsnouveau.common.entity.goal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

import java.util.function.Supplier;

public class ConditionalMeleeGoal extends MeleeAttackGoal {
    public Supplier<Boolean> canUse;

    public ConditionalMeleeGoal(PathfinderMob pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen, Supplier<Boolean> canUse) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        this.canUse = canUse;
    }

    @Override
    public boolean canUse() {
        return canUse.get() && super.canUse();
    }
}
