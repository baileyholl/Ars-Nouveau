package com.hollingsworth.arsnouveau.common.spell.augment;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;

public class AugmentSensitive extends AbstractAugment {
    public static AugmentSensitive INSTANCE = new AugmentSensitive();

    private AugmentSensitive() {
        super(GlyphLib.AugmentSensitiveID, "Sensitive");
    }

    @Override
    public String getBookDescription() {
        return "Causes forms to target blocks they normally cannot target. Projectile and Orbit will target grass, and Touch will target fluids and air. Changes targeting rules of certain effects.";
    }

    @Override
    public SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart) {
        builder.setSensitive();
        return builder;
    }

    @Override
    public int getDefaultManaCost() {
        return 10;
    }
}
