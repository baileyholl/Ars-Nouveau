package com.hollingsworth.craftedmagic.spell.augment;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AbstractAugment;

public class AugmentSplit extends AbstractAugment {
    public AugmentSplit() {
        super(ModConfig.AugmentSplitID, "Split");
    }

    @Override
    public int getManaCost() {
        return 20;
    }

    @Override
    public Tier getTier() {
        return  Tier.THREE;
    }
}
