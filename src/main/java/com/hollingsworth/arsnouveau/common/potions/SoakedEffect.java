package com.hollingsworth.arsnouveau.common.potions;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class SoakedEffect extends MobEffect {

    public SoakedEffect() {
        super(MobEffectCategory.NEUTRAL, 0x0000FF, ParticleTypes.DRIPPING_WATER);
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity living, int amplifier) {
        super.applyEffectTick(living, amplifier);
        if (living.isOnFire()) {
            living.clearFire();
        }
        return true;
    }

}
