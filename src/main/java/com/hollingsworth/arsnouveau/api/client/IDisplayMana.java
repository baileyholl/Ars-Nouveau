package com.hollingsworth.arsnouveau.api.client;

import net.minecraft.world.item.ItemStack;

public interface IDisplayMana {
    /**
     * If the held itemstack should display the mana bar
     */
    default boolean shouldDisplay(ItemStack stack) {
        return true;
    }
}
