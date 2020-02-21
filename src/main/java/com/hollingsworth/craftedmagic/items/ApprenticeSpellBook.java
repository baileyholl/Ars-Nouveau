package com.hollingsworth.craftedmagic.items;

import com.hollingsworth.craftedmagic.ArsNouveau;
import net.minecraft.item.Item;

public class ApprenticeSpellBook extends SpellBook{
    public ApprenticeSpellBook(){
        super();
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }
}
