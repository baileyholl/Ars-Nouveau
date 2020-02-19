package com.hollingsworth.craftedmagic.spell.augment;

import com.hollingsworth.craftedmagic.api.spell.AbstractAugment;

public class AugmentDuplicate extends AbstractAugment {
    public AugmentDuplicate(String tag, String description) {
        super(tag, description);
    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
