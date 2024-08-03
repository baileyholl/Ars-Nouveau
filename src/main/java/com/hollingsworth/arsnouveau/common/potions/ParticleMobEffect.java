package com.hollingsworth.arsnouveau.common.potions;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public abstract class ParticleMobEffect extends PublicEffect {

    protected ParticleMobEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean applyEffectTick(LivingEntity living, int amplifier) {
        if (living.level.isClientSide)
            if (living.level.random.nextInt(getChance()) == 0) {
                living.level.addParticle(getParticle(), living.getRandomX(1.0D), living.getRandomY(), living.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
            }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return true;
    }

    public abstract ParticleOptions getParticle();

    public int getChance() {
        return 3;
    }

}