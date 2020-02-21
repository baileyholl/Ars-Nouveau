package com.hollingsworth.craftedmagic.spell.augment;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AbstractAugment;

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
        return Tier.THREE;
    }
}
