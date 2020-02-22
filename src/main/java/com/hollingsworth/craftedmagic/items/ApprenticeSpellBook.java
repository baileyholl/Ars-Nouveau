package com.hollingsworth.craftedmagic.items;

public class ApprenticeSpellBook extends SpellBook{
    public ApprenticeSpellBook(){
        super();
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }
}
