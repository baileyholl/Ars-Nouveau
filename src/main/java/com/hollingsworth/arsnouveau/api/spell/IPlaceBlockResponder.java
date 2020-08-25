package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.item.ItemStack;

public interface IPlaceBlockResponder {
    /**
     * Called when an attempt to place a block is made. This is used by the PlaceBlock spell as a way to support automation entities.
     * @return Returns the itemstack that will attempt to be placed.
     */
    public ItemStack onPlaceBlock();

}
