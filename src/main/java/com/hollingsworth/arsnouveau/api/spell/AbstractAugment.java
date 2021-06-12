package com.hollingsworth.arsnouveau.api.spell;

import javax.annotation.Nonnull;
import java.util.Set;

public abstract class AbstractAugment extends AbstractSpellPart {

    public AbstractAugment(String tag, String description) {
        super(tag, description);
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }

    @Override
    abstract public int getManaCost();
}
