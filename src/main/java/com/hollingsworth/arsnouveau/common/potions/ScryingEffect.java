package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class ScryingEffect extends MobEffect {
    protected ScryingEffect() {
        super(MobEffectCategory.BENEFICIAL, 2039587);
        setRegistryName(ArsNouveau.MODID, "scrying");
    }
}
