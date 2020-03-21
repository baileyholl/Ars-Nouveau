package com.hollingsworth.arsnouveau.api.spell;

public abstract class AbstractAugment extends AbstractSpellPart {

    public AbstractAugment(String tag, String description) {
        super(tag, description);
    }

    @Override
    abstract public int getManaCost();
}
