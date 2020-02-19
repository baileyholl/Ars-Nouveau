package com.hollingsworth.craftedmagic.spell.augment;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AbstractAugment;

public class AugmentDampen extends AbstractAugment {

    public AugmentDampen() {
        super(ModConfig.AugmentDampenID, "Dampen");
    }

    @Override
    public int getManaCost() {
        return -5;
    }
}
