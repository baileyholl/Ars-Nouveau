package com.hollingsworth.arsnouveau.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;


public class ShieldPotion extends Effect {
    public ShieldPotion() {
        super(EffectType.BENEFICIAL, 2039587);
        setRegistryName(ArsNouveau.MODID, "shield");
    }


}
