package com.hollingsworth.craftedmagic.items;

public class ArchmageSpellBook extends SpellBook{

    public ArchmageSpellBook(){
        super();
    }

    @Override
    public Tier getTier() {
        return Tier.THREE;
    }
}
