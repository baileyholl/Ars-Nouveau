package com.hollingsworth.arsnouveau.api.mana;

import net.minecraft.world.item.ItemStack;

public interface IManaEquipment{

    @Deprecated // To be removed for itemstack sensitive version
    default int getMaxManaBoost(){
        return 0;
    }

    default int getMaxManaBoost(ItemStack i){
        return getMaxManaBoost();
    }

    @Deprecated
    default int getManaRegenBonus(){
        return 0;
    }

    default int getManaRegenBonus(ItemStack i){
        return getManaRegenBonus();
    }

    @Deprecated
    default int getManaDiscount(){
        return 0;
    }

    default int getManaDiscount(ItemStack i){
        return getManaDiscount();
    }

}
