package com.hollingsworth.craftedmagic.spell.augment;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AugmentType;

public class AugmentExtendTime extends AugmentType {
    public AugmentExtendTime() {
        super(ModConfig.AugmentExtendTimeID, "Extend Time");
    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
