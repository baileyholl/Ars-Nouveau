package com.hollingsworth.craftedmagic.spell.augment;

import com.hollingsworth.craftedmagic.ModConfig;

public class AugmentPierce extends AugmentType {
    public AugmentPierce() {
        super(ModConfig.AugmentPierceID, "Pierce");
    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
