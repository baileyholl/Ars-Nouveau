package com.hollingsworth.craftedmagic.items;

import com.hollingsworth.craftedmagic.ArsNouveau;
import net.minecraft.item.Item;

public class AdvancedSpellBook extends SpellBook{
    public AdvancedSpellBook(){
        super();
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }
}
