package com.hollingsworth.craftedmagic.spell.augment;

import com.hollingsworth.craftedmagic.ModConfig;

public class AugmentEmpower extends AugmentType{
    public AugmentEmpower() {
        super(ModConfig.AugmentEmpower, "Empower");
    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
