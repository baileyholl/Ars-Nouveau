package com.hollingsworth.craftedmagic.spell.augment;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AugmentType;

public class AugmentDampen extends AugmentType {

    public AugmentDampen() {
        super(ModConfig.AugmentDampenID, "Dampen");
    }

    @Override
    public int getManaCost() {
        return -5;
    }
}
