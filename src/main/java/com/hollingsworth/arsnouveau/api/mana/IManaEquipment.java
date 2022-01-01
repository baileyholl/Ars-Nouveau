package com.hollingsworth.arsnouveau.api.mana;

import net.minecraft.world.item.ItemStack;

public interface IManaEquipment{

    default int getMaxManaBoost(ItemStack i){
        return 0;
    }

    default int getManaRegenBonus(ItemStack i){
        return 0;
    }

    default int getManaDiscount(ItemStack i){
        return 0;
    }

}
