package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Used by spellcasting entities or blocks that support receiving items.
 * See #EffectPickup
 */
@Deprecated
public interface IPickupResponder extends IInventoryResponder {

    /**
     * Called when an attempt to pickup loot is made. This is primarily used by EffectPickup for giving items to objects and non-player entities.
     *
     * @param stack Itemstack that will attempt to be put into the inventory.
     * @return Returns the resulting itemstack
     */
    @Deprecated
   @NotNull
    ItemStack onPickup(ItemStack stack);
}
