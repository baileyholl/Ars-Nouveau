package com.hollingsworth.arsnouveau.spell.augment;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;

public class AugmentAmplify extends AbstractAugment {
    public AugmentAmplify() {
        super(ModConfig.AugmentAmplifyID, "Amplify");
    }

    @Override
    public int getManaCost() {
        return 20;
    }

    @Override
    public Tier getTier() {
        return Tier.ONE;
    }
}
