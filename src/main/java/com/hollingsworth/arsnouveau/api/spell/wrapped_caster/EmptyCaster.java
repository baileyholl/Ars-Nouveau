package com.hollingsworth.arsnouveau.api.spell.wrapped_caster;

import com.hollingsworth.arsnouveau.api.spell.SpellContext;

public class EmptyCaster implements IWrappedCaster{
    @Override
    public SpellContext.CasterType getCasterType() {
        return SpellContext.CasterType.OTHER;
    }

    @Override
    public void expendMana(int totalCost) {

    }
}
