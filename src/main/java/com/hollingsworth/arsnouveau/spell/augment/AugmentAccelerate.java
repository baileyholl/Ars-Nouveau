package com.hollingsworth.arsnouveau.spell.augment;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;

public class AugmentAccelerate extends AbstractAugment {

    public AugmentAccelerate() {
        super(ModConfig.AugmentAccelerateID, "Accelerate");
    }

    @Override
    public int getManaCost() {
        return 10;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }
}
