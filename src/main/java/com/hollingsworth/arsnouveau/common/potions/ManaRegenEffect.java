package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class ManaRegenEffect extends Effect {
    protected ManaRegenEffect() {
        super(EffectType.BENEFICIAL, 8080895);
        setRegistryName(ArsNouveau.MODID, "mana_regen");
    }
}
