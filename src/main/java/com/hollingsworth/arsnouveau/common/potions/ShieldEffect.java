package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;


public class ShieldEffect extends Effect {
    public ShieldEffect() {
        super(EffectType.BENEFICIAL, 2039587);
        setRegistryName(ArsNouveau.MODID, "shield");
    }


}
