package com.hollingsworth.arsnouveau.common.entity.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;

import java.util.function.Supplier;

public class ConditionalLookAtMob extends LookAtPlayerGoal {
    Supplier<Boolean> canContinue;
    public ConditionalLookAtMob(Mob pMob, Class<? extends LivingEntity> pLookAtType, float pLookDistance, Supplier<Boolean> canContinue) {
        super(pMob, pLookAtType, pLookDistance);
        this.canContinue = canContinue;
    }

    public ConditionalLookAtMob(Mob pMob, Class<? extends LivingEntity> pLookAtType, float pLookDistance, float pProbability, Supplier<Boolean> canContinue) {
        super(pMob, pLookAtType, pLookDistance, pProbability);
        this.canContinue = canContinue;
    }

    public ConditionalLookAtMob(Mob pMob, Class<? extends LivingEntity> pLookAtType, float pLookDistance, float pProbability, boolean pOnlyHorizontal, Supplier<Boolean> canContinue) {
        super(pMob, pLookAtType, pLookDistance, pProbability, pOnlyHorizontal);
        this.canContinue = canContinue;
    }

    @Override
    public boolean canUse() {
        return this.canContinue.get() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return this.canContinue.get() && super.canContinueToUse();
    }
}
