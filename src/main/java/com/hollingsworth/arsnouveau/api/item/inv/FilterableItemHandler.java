package com.hollingsworth.arsnouveau.api.item.inv;

import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Represents an ItemHandler and its list of filters.
 */
public class FilterableItemHandler {
    private SlotCache slotCache;
    private IItemHandler handler;
    public FilterSet filters;

    public FilterableItemHandler(IItemHandler handler) {
        this(handler, new FilterSet.ListSet());
    }

    public FilterableItemHandler(IItemHandler handler, List<Function<ItemStack, ItemScroll.SortPref>> functions) {
        this(handler, new FilterSet.ListSet(functions));
    }

    public FilterableItemHandler(IItemHandler handler, FilterSet filters) {
        this.handler = handler;
        this.filters = filters;
        this.slotCache = new SlotCache();
    }

    public FilterableItemHandler withSlotCache(SlotCache cache) {
        this.slotCache = cache;
        return this;
    }

    /**
     * If this inventory supports insertion of the given stack.
     */
    public InteractResult canInsert(ItemStack stack) {
        ItemScroll.SortPref pref = getHighestPreference(stack);
        return new InteractResult(pref, pref != ItemScroll.SortPref.INVALID);
    }

    /**
     * If this inventory supports extraction of the given stack.
     */
    public InteractResult canExtract(ItemStack stack) {
        ItemScroll.SortPref pref = getHighestPreference(stack);
        return new InteractResult(pref, pref != ItemScroll.SortPref.INVALID);
    }

    /**
     * If this inventory supports extraction or insertion of the given stack.
     */
    public InteractResult canInteractFor(ItemStack stack, InteractType type) {
        return type == InteractType.EXTRACT ? canExtract(stack) : canInsert(stack);
    }

    /**
     * Returns the highest preference from a list of predicates, unless it is invalid.
     * Invalid overrules all other preferences, as the user does NOT want that item to be inserted.
     */
    public ItemScroll.SortPref getHighestPreference(ItemStack stack) {
        return filters.getHighestPreference(stack);
    }

    public IItemHandler getHandler() {
        return handler;
    }

    /**
     * Inserts the ItemStack into the inventory, filling up already present stacks first.
     * This is equivalent to the behaviour of a player picking up an item.
     * Note: This function stacks items without subtypes with different metadata together.
     */
    public ItemStack insertItemStacked(ItemStack stack, boolean simulate) {
        IItemHandler inventory = handler;
        if (inventory == null || stack.isEmpty())
            return stack;

        // not stackable -> just insert into a new slot
        if (!stack.isStackable()) {
            return insertItem(stack, simulate);
        }

        int sizeInventory = inventory.getSlots();
        stack = insertUsingCache(stack, simulate);
        if (stack.isEmpty()) {
            return stack;
        }
        Collection<Integer> slotsForStack = slotCache.getOrCreateSlots(stack.getItem());
        Collection<Integer> emptySlots = slotCache.getOrCreateSlots(Items.AIR);
        // Iterate all slots until our stack is empty, caching along the way
        for (int i = 0; i < sizeInventory; i++) {
            ItemStack slot = inventory.getStackInSlot(i);
            if (ItemStack.isSameItemSameComponents(slot, stack)) {
                int count = stack.getCount();
                stack = inventory.insertItem(i, stack, simulate);
                if (stack.getCount() != count) {
                    slotsForStack.add(i);
                }
                if (stack.isEmpty()) {
                    return stack;
                }
            } else if (slot.isEmpty()) {
                emptySlots.add(i);
            }
        }

        List<Integer> invalidSlots = new ArrayList<>();
        // If we have exhausted inserting
        for (int slot : emptySlots) {
            if (inventory.getStackInSlot(slot).isEmpty()) {
                stack = inventory.insertItem(slot, stack, simulate);
                if (stack.isEmpty()) {
                    break;
                }
            } else {
                invalidSlots.add(slot);
            }
        }

        for (int slot : invalidSlots) {
            emptySlots.remove(slot);
        }


        return stack;
    }

    /**
     * Inserts into any valid cached slots, then inserts into any empty cached slots.
     * If no cached slots are available, it will insert into any empty slots.
     * Optimizes non-stackable item insertion.
     */
    private ItemStack insertItem(ItemStack stack, boolean simulate) {
        IItemHandler dest = handler;
        if (dest == null || stack.isEmpty())
            return stack;

        stack = insertUsingCache(stack, simulate);
        if (stack.isEmpty()) {
            return stack;
        }

        stack = insertInCachedEmptySlots(stack, simulate);

        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        Item item = stack.getItem();
        // Iterate all slots until our stack is empty, caching along the way
        for (int i = 0; i < dest.getSlots(); i++) {
            int count = stack.getCount();
            stack = dest.insertItem(i, stack, simulate);
            if (stack.getCount() != count) {
                slotCache.getOrCreateSlots(item).add(i);

            }

            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
            ItemStack targetStack = dest.getStackInSlot(i);
            if (targetStack.isEmpty()) {
                slotCache.getOrCreateSlots(Items.AIR).add(i);
            }
        }

        return stack;
    }

    /**
     * Inserts into cached slots if any and invalidates slots.
     * If there are no cached slots, this does nothing.
     */
    private ItemStack insertUsingCache(ItemStack stack, boolean simulate) {
        IItemHandler dest = handler;

        Collection<Integer> slots = slotCache.getIfPresent(stack.getItem());

        if (slots == null || stack.isEmpty()) {
            return stack;
        }

        List<Integer> invalidSlots = new ArrayList<>();
        int maxSlots = dest.getSlots();
        for (int slot : slots) {
            if (slot >= maxSlots) {
                invalidSlots.add(slot);
                continue;
            }

            int count = stack.getCount();
            ItemStack targetStack = dest.getStackInSlot(slot);
            // If this stack wants to stack and our cached slot is air but the slot is not, this means our item has moved locations.
            // If we blindly insert here, we can create partial stacks instead of combining.
            // If this is a non-stackable, ignore this and insert anywhere because our slot list is air.
            if (stack.isStackable() && targetStack.isEmpty()) {
                invalidSlots.add(slot);
                continue;
            }
            stack = dest.insertItem(slot, stack, simulate);
            if (stack.getCount() == count) {
                if (!ItemStack.isSameItemSameComponents(targetStack, stack)) {
                    invalidSlots.add(slot);
                }
            }

            if (stack.isEmpty()) {
                break;
            }
        }

        for (int slot : invalidSlots) {
            slots.remove(slot);
        }

        return stack;
    }

    private ItemStack insertInCachedEmptySlots(ItemStack stack, boolean simulate) {
        IItemHandler dest = handler;
        Collection<Integer> slots = slotCache.getIfPresent(Items.AIR);
        if (slots == null) {
            return stack;
        }

        List<Integer> invalidSlots = new ArrayList<>();
        int maxSlots = dest.getSlots();
        for (int slot : slots) {
            if (slot >= maxSlots) {
                invalidSlots.add(slot);
                continue;
            }
            int count = stack.getCount();
            stack = dest.insertItem(slot, stack, simulate);
            if (stack.getCount() == count) {
                invalidSlots.add(slot);
            } else {
                // If we successfully inserted into an empty slot, cache the inserted item slot and remove it from the empty slot list.
                if (!dest.getStackInSlot(slot).isEmpty()) {
                    slotCache.getOrCreateSlots(stack.getItem()).add(slot);
                    invalidSlots.add(slot);
                }
            }
            if (stack.isEmpty()) {
                break;
            }
        }

        for (int slot : invalidSlots) {
            slots.remove(slot);
        }

        return stack;
    }
}
