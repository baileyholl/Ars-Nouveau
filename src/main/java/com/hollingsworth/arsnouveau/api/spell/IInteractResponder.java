package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.item.ItemStack;

/**
 * Used by spell casters that support the Interact spell
 */
public interface IInteractResponder {
    /**
     * @return the held item used by the fake player.
     */
    ItemStack getHeldItem();

}
