package com.hollingsworth.arsnouveau.common.potions;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SoggyEffect extends ParticleMobEffect {

    public SoggyEffect() {
        super(MobEffectCategory.NEUTRAL, 0x0000FF);
    }

    @Override
    public void applyEffectTick(LivingEntity living, int amplifier) {
        super.applyEffectTick(living, amplifier);
        if (living.isOnFire()) {
            living.clearFire();
        }
    }

    @Override
    public ParticleOptions getParticle() {
        return ParticleTypes.DRIPPING_WATER;
    }

    @Override
    public int getChance() {
        return 4;
    }
}
