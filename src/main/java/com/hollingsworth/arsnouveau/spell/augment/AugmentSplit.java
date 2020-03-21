package com.hollingsworth.arsnouveau.spell.augment;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;

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
