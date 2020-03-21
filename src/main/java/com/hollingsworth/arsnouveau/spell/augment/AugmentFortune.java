package com.hollingsworth.arsnouveau.spell.augment;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;

public class AugmentFortune extends AbstractAugment {
    public AugmentFortune() {
        super(ModConfig.AugmentFortuneID, "Fortune");
    }

    @Override
    public int getManaCost() {
        return 40;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }
}
