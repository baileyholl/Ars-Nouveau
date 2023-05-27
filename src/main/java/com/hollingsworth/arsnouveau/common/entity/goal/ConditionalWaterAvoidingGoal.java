package com.hollingsworth.arsnouveau.common.entity.goal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;

import java.util.function.Supplier;

public class ConditionalWaterAvoidingGoal extends WaterAvoidingRandomStrollGoal {
    Supplier<Boolean> canUse;

    public ConditionalWaterAvoidingGoal(PathfinderMob pMob, double pSpeedModifier, Supplier<Boolean> canUse) {
        super(pMob, pSpeedModifier);
        this.canUse = canUse;

    }

    public ConditionalWaterAvoidingGoal(PathfinderMob pMob, double pSpeedModifier, float pProbability, Supplier<Boolean> canUse) {
        super(pMob, pSpeedModifier, pProbability);
        this.canUse = canUse;
    }

    @Override
    public boolean canUse() {
        return this.canUse.get() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse.get() && super.canContinueToUse();
    }
}
