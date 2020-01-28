package com.hollingsworth.craftedmagic.spell.augment;

import com.hollingsworth.craftedmagic.api.AbstractSpellPart;

public abstract class AugmentType extends AbstractSpellPart {

    public AugmentType(String tag, String description) {
        super(tag, description);
    }

    @Override
    abstract public int getManaCost();
}
