package com.hollingsworth.arsnouveau.api.item.inv;

import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public interface IMapInventory {

    /**
     * Inserts a stack anywhere in the inventory, with preference to existing matching stacks.
     * Implementers are expected to utilize a cache or map to optimize performance.
     * Expected operation for Dirt:
     * Insert in any existing slots for Dirt, then insert the remainder into any empty slots.
     * @return the remainder
     */
    ItemStack insertStack(ItemStack stack, boolean simulate);

    boolean hasExistingSlotsForInsertion(ItemStack stack);

    default ItemStack extractByItem(Item item, int count, boolean simulate){
        return extractByItem(item, count, simulate, stack -> true);
    }

    ItemStack extractByItem(Item item, int count, boolean simulate, Predicate<ItemStack> filter);

    ItemScroll.SortPref getInsertionPreference(ItemStack stack);




}
