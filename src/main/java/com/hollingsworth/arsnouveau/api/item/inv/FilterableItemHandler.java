package com.hollingsworth.arsnouveau.api.item.inv;

import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

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

    public FilterableItemHandler(IItemHandler handler, FilterSet filters, SlotCache cache) {
        this.handler = handler;
        this.filters = filters;
        this.slotCache = cache;
    }

    public FilterableItemHandler(IItemHandler handler, FilterSet filters) {
        this(handler, filters, new SlotCache(handler.getSlots()));
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
        return this.slotCache.insertItemStacked(this.handler, stack, simulate);
    }
}
