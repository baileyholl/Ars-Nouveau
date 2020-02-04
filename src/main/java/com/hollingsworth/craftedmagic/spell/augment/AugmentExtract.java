package com.hollingsworth.craftedmagic.spell.augment;

import com.hollingsworth.craftedmagic.ModConfig;

public class AugmentExtract extends AugmentType{

    public AugmentExtract() {
        super(ModConfig.AugmentExtractID, "Extract");
    }

    @Override
    public int getManaCost() {
        return 20;
    }
}
