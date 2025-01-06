package com.hollingsworth.arsnouveau.api.perk;

import net.minecraft.world.item.ItemStack;

/**
 * Returns a perk holder serialized from T
 * You should not implement this interface directly. Use a non-generic child interface.
 */
@FunctionalInterface
public interface IPerkProvider {

    IPerkHolder getPerkHolder(ItemStack obj);

}
