package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.item.ISpellModifier;

import javax.annotation.Nonnull;
import java.util.Set;

public abstract class AbstractAugment extends AbstractSpellPart implements ISpellModifier {

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

    public SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart){
        return builder;
    }
}
