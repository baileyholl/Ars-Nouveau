package com.hollingsworth.arsnouveau.common.spell.augment;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;

public class AugmentDurationDown extends AbstractAugment {
    public static AugmentDurationDown INSTANCE = new AugmentDurationDown();

    private AugmentDurationDown() {
        super(GlyphLib.AugmentReduceTime, "Reduce Time");
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }


    @Override
    public int getDefaultManaCost() {
        return 15;
    }

    @Override
    public SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart) {
        builder.addDurationModifier(-1.0d);
        return super.applyModifiers(builder, spellPart);
    }

    @Override
    public String getBookDescription() {
        return "Reduces the duration of spells like potion effects, delay, redstone signal, and others.";
    }
}
