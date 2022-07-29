package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class RecoveryEffect extends MobEffect {
    public RecoveryEffect() {
        super(MobEffectCategory.BENEFICIAL, new ParticleColor(0, 200, 40).getColor());
    }
}
