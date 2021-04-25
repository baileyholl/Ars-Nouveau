package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class HexEffect extends Effect {
    protected HexEffect() {
        super(EffectType.HARMFUL, 8080895);
        setRegistryName(ArsNouveau.MODID, "hex");
    }
}
