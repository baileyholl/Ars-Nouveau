package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.Spell;

public class FormSpellArrow extends SpellArrow{
    public FormSpellArrow(String registryName, AbstractAugment augment, int numParts) {
        super(registryName, augment, numParts);
    }

    @Override
    public void modifySpell(Spell spell) {
        for(int i = 0; i < numParts; i++) {
            spell.recipe.add(1,this.part);
        }
    }
}
