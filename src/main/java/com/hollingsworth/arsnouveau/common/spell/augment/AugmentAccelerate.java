package com.hollingsworth.arsnouveau.common.spell.augment;

import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;

public class AugmentAccelerate extends AbstractAugment {
    public static AugmentAccelerate INSTANCE = new AugmentAccelerate();

    private AugmentAccelerate() {
        super(GlyphLib.AugmentAccelerateID, "Accelerate");
    }

    @Override
    public int getDefaultManaCost() {
        return 10;
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.TWO;
    }

    @Override
    public String getBookDescription() {
        return "Increases the speed of projectile spells.";
    }
}
