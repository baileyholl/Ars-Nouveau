package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.api.item.AbstractAugmentItem;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;

public class RingOfAugment extends AbstractAugmentItem {
    public RingOfAugment(String registryName) {
        super(registryName);
    }

    @Override
    public AbstractAugment getBonusAugment() {
        return null;
    }

    @Override
    public int getBonusLevel() {
        return 0;
    }
}
