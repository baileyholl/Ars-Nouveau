package com.hollingsworth.arsnouveau.api.item.inv;

import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.function.Predicate;

public interface IMapInventory extends IItemHandler {

    /**
     * Inserts a stack anywhere in the inventory, with preference to existing matching stacks.
     * Implementers are expected to utilize a cache or map to optimize performance.
     * Expected operation for Dirt:
     * Insert in any existing slots for Dirt, then insert the remainder into any empty slots.
     *
     * @return the remainder
     */
    ItemStack insertStack(ItemStack stack, boolean simulate);

    /**
     * Determines if there is an existing slot that can accept the item for insertion.
     * This does not guarantee that the item will be inserted, as it may not fit in the existing stacks or slot, just
     * that this item exists in the inventory and would be valid for insertion.
     */
    boolean hasExistingSlotsForInsertion(ItemStack stack);

    default ItemStack extractByItem(Item item, int count, boolean simulate) {
        return extractByItem(item, count, simulate, stack -> true);
    }

    ItemStack extractByItem(Item item, int count, boolean simulate, Predicate<ItemStack> filter);

    ItemScroll.SortPref getInsertionPreference(ItemStack stack);

    @Override
    default ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return insertStack(stack, simulate);
    }

    @Override
    default ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    default int getSlotLimit(int slot) {
        return 0;
    }

    @Override
    default ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    default int getSlots() {
        return 0;
    }

    @Override
    default boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }
}
