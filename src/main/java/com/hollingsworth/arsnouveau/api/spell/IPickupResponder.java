package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.item.ItemStack;

public interface IPickupResponder extends IInventoryResponder{

    /**
     * Called when an attempt to pickup loot is made. This is primarily used by EffectPickup for giving items to objects and non-player entities.
     * @param stack Itemstack that will attempt to be put into the inventory.
     * @return Returns the resulting itemstack
     */
    ItemStack onPickup(ItemStack stack);
}
