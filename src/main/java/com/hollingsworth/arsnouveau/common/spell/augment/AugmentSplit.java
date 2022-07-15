package com.hollingsworth.arsnouveau.common.spell.augment;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;

public class AugmentSplit extends AbstractAugment {
    public static AugmentSplit INSTANCE = new AugmentSplit();

    private AugmentSplit() {
        super(GlyphLib.AugmentSplitID, "Split");
    }

    @Override
    public int getDefaultManaCost() {
        return 20;
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.THREE;
    }

    @Override
    public String getBookDescription() {
        return "Causes multiple projectiles to be cast at once. Each projectile applies a set of effects.";
    }
}
