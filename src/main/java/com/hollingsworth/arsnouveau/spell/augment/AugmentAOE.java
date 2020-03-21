package com.hollingsworth.arsnouveau.spell.augment;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;

public class AugmentAOE extends AbstractAugment {
    public AugmentAOE() {
        super(ModConfig.AugmentAOEID, "AOE");
    }

    @Override
    public int getManaCost() {
        return 40;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }
}
