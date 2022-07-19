package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class BlastEffect extends MobEffect {
    public BlastEffect() {
        super(MobEffectCategory.HARMFUL, new ParticleColor(250, 0, 0).getColor());
    }
}
