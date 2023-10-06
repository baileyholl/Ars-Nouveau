package com.hollingsworth.arsnouveau.api.mana;

import net.minecraft.world.item.ItemStack;


//TODO split IManaBoost and IManaDiscount
public interface IManaEquipment extends IManaDiscountEquipment{

    default int getMaxManaBoost(ItemStack i) {
        return 0;
    }

    default int getManaRegenBonus(ItemStack i) {
        return 0;
    }

}
