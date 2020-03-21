package com.hollingsworth.arsnouveau.items;

public class ApprenticeSpellBook extends SpellBook{
    public ApprenticeSpellBook(){
        super();
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }
}
