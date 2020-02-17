package com.hollingsworth.craftedmagic.spell.augment;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AugmentType;

public class AugmentAccelerate extends AugmentType {

    public AugmentAccelerate() {
        super(ModConfig.AugmentAccelerateID, "Accelerate");
    }

    @Override
    public int getManaCost() {
        return 5;
    }
}
