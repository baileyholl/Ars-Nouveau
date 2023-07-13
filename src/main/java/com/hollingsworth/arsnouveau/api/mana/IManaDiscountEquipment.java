package com.hollingsworth.arsnouveau.api.mana;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import net.minecraft.world.item.ItemStack;

public interface IManaDiscountEquipment {

    //not spell sensitive
    default int getManaDiscount(ItemStack i) {
        return getManaDiscount(i, new Spell());
    }

    //spell sensitive
    default int getManaDiscount(ItemStack i, Spell spell) {
        return 0;
    }
}
