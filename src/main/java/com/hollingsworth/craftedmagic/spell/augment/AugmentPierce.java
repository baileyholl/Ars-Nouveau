package com.hollingsworth.craftedmagic.spell.augment;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AbstractAugment;

public class AugmentPierce extends AbstractAugment {
    public AugmentPierce() {
        super(ModConfig.AugmentPierceID, "Pierce");
    }

    @Override
    public int getManaCost() {
        return 20;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }
}
