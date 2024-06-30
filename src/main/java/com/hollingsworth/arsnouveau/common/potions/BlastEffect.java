package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class BlastEffect extends MobEffect {
    public BlastEffect() {
        super(MobEffectCategory.HARMFUL, new ParticleColor(250, 0, 0).getColor());
    }

    @Override
    public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        explode(pLivingEntity, pAmplifier);
        return true;
    }

    public static void explode(LivingEntity pLivingEntity, int pAmplifier) {
        pLivingEntity.level.explode(null, pLivingEntity.getX(), pLivingEntity.getY() + 1, pLivingEntity.getZ(), 2.0f + pAmplifier, false, Level.ExplosionInteraction.NONE);
    }
}
