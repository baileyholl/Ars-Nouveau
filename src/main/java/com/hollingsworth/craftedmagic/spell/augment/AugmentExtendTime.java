package com.hollingsworth.craftedmagic.spell.augment;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AbstractAugment;

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
