package com.hollingsworth.arsnouveau.spell.augment;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;

public class AugmentDampen extends AbstractAugment {

    public AugmentDampen() {
        super(ModConfig.AugmentDampenID, "Dampen");
    }

    @Override
    public int getManaCost() {
        return -5;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }
}
