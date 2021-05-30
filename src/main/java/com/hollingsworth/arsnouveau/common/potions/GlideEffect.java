package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class GlideEffect extends Effect {
    protected GlideEffect() {
        super(EffectType.BENEFICIAL, 8080895);
        setRegistryName(ArsNouveau.MODID, "glide");
    }
}
