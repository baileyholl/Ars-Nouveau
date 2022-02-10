package com.hollingsworth.arsnouveau.common.spell.augment;

import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;

public class AugmentAOE extends AbstractAugment {
    public static AugmentAOE INSTANCE = new AugmentAOE();

    private AugmentAOE() {
        super(GlyphLib.AugmentAOEID, "AOE");
    }

    @Override
    public int getDefaultManaCost() {
        return 35;
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.TWO;
    }

    @Override
    public String getBookDescription() {
        return "Spells will affect a larger area around a targeted block.";
    }
}
