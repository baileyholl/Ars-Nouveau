package com.hollingsworth.arsnouveau.spell.augment;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;

public class AugmentExtendTime extends AbstractAugment {
    public AugmentExtendTime() {
        super(ModConfig.AugmentExtendTimeID, "Extend Time");
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
