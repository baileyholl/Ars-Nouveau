package com.hollingsworth.arsnouveau.api.client;

import net.minecraft.item.ItemStack;

public interface IDisplayMana {

    default boolean shouldDisplay(ItemStack stack){
        return true;
    }
}
