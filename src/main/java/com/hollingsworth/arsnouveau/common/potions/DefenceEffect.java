package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class DefenceEffect extends MobEffect {
    protected DefenceEffect() {
        super(MobEffectCategory.BENEFICIAL, new ParticleColor(150, 0, 150).getColor());
    }
}
