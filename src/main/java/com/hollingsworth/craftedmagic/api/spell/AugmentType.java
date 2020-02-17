package com.hollingsworth.craftedmagic.api.spell;

public abstract class AugmentType extends AbstractSpellPart {

    public AugmentType(String tag, String description) {
        super(tag, description);
    }

    @Override
    abstract public int getManaCost();
}
