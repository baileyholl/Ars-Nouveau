package com.hollingsworth.craftedmagic.spell.augment;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AbstractAugment;

public class AugmentAmplify extends AbstractAugment {
    public AugmentAmplify() {
        super(ModConfig.AugmentAmplifyID, "Amplify");
    }

    @Override
    public int getManaCost() {
        return 15;
    }
}
