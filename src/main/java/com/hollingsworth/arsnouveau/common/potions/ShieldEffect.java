package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;


public class ShieldEffect extends MobEffect {
    public ShieldEffect() {
        super(MobEffectCategory.BENEFICIAL, 2039587);
        setRegistryName(ArsNouveau.MODID, "shield");
    }


}
