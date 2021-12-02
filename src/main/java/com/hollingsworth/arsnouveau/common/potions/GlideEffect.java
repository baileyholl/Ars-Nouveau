package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class GlideEffect extends MobEffect {
    protected GlideEffect() {
        super(MobEffectCategory.BENEFICIAL, 8080895);
        setRegistryName(ArsNouveau.MODID, "glide");
    }
}
