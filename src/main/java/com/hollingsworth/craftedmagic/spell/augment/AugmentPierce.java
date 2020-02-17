package com.hollingsworth.craftedmagic.spell.augment;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AugmentType;

public class AugmentPierce extends AugmentType {
    public AugmentPierce() {
        super(ModConfig.AugmentPierceID, "Pierce");
    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
