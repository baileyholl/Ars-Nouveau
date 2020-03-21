package com.hollingsworth.arsnouveau.spell.augment;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;

public class AugmentPierce extends AbstractAugment {
    public AugmentPierce() {
        super(ModConfig.AugmentPierceID, "Pierce");
    }

    @Override
    public int getManaCost() {
        return 20;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }
}
