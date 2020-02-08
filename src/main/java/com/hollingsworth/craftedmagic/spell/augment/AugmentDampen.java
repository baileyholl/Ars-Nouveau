package com.hollingsworth.craftedmagic.spell.augment;

import com.hollingsworth.craftedmagic.ModConfig;

public class AugmentDampen extends AugmentType{

    public AugmentDampen() {
        super(ModConfig.AugmentDampenID, "Dampen");
    }

    @Override
    public int getManaCost() {
        return -5;
    }
}
