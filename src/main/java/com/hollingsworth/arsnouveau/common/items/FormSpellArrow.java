package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.Spell;

public class FormSpellArrow extends SpellArrow {
    public FormSpellArrow(AbstractAugment augment, int numParts) {
        super(augment, numParts);
    }

    @Override
    public void modifySpell(Spell.Mutable spell) {
        if(spell.recipe.isEmpty())
            return;
        for (int i = 0; i < numParts; i++) {
            spell.recipe.add(1, this.part);
        }
    }
}
