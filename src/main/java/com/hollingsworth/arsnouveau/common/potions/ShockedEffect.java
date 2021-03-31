package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class ShockedEffect extends Effect {

    public ShockedEffect() {
        super(EffectType.HARMFUL, 2039587);
        setRegistryName(ArsNouveau.MODID, "shocked");
    }

}
