package com.hollingsworth.arsnouveau.common.entity.goal.chimera;

import com.hollingsworth.arsnouveau.common.entity.goal.ConditionalMeleeGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;

import java.util.function.Supplier;

public class ChimeraMeleeGoal extends ConditionalMeleeGoal {
    public ChimeraMeleeGoal(PathfinderMob pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen, Supplier<Boolean> canUse) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen, canUse);
    }

    @Override
    protected double getAttackReachSqr(LivingEntity pAttackTarget) {
        return 10;
    }
}
