package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class FreezingEffect extends MobEffect {
    protected FreezingEffect() {
        super(MobEffectCategory.HARMFUL, new ParticleColor(0, 0, 250).getColor());
    }
}
