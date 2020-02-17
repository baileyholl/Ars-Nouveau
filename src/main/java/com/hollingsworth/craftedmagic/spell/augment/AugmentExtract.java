package com.hollingsworth.craftedmagic.spell.augment;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AugmentType;

public class AugmentExtract extends AugmentType {

    public AugmentExtract() {
        super(ModConfig.AugmentExtractID, "Extract");
    }

    @Override
    public int getManaCost() {
        return 20;
    }
}
