package com.hollingsworth.arsnouveau.common.spell.augment;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;

public class AugmentAmplify extends AbstractAugment {
    public static AugmentAmplify INSTANCE = new AugmentAmplify();


    private AugmentAmplify() {
        super(GlyphLib.AugmentAmplifyID, "Amplify");
    }

    @Override
    public int getDefaultManaCost() {
        return 20;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.ONE;
    }

    @Override
    public SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart) {
        builder.addAmplification(1.0);
        return super.applyModifiers(builder, spellPart);
    }

    @Override
    public String getBookDescription() {
        return "Additively increases the power of most spell effects. Can increase the harvest level of Break and increases the damage of spells.";
    }
}
