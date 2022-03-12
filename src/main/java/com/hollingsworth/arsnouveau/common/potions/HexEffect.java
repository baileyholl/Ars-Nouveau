package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class HexEffect extends MobEffect {
    protected HexEffect() {
        super(MobEffectCategory.HARMFUL, 8080895);
        setRegistryName(ArsNouveau.MODID, "hex");
    }
}
