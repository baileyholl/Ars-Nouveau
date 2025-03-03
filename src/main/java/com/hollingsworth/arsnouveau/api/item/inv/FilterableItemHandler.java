package com.hollingsworth.arsnouveau.api.item.inv;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents an ItemHandler and its list of filters.
 */
public class FilterableItemHandler {
    private LoadingCache<Item, HashSet<Integer>> slotCache;
    private IItemHandler handler;
    private List<Function<ItemStack, ItemScroll.SortPref>> filters;
    private @Nullable Supplier<BlockPos> posSupplier;

    public FilterableItemHandler(IItemHandler handler){
        this(handler, new ArrayList<>());
    }

    public FilterableItemHandler(IItemHandler handler, List<Function<ItemStack, ItemScroll.SortPref>> filters){
        this(handler, filters, null);
    }

    public FilterableItemHandler(IItemHandler handler, List<Function<ItemStack, ItemScroll.SortPref>> filters, @Nullable Supplier<BlockPos> posSupplier){
        this.handler = handler;
        this.filters = filters;
        this.posSupplier = posSupplier;
        this.slotCache = CacheBuilder.newBuilder().maximumSize(100).build(CacheLoader.from((key) -> new HashSet<>()));
    }

    public @Nullable BlockPos getPos(){
        return posSupplier == null ? null : posSupplier.get();
    }

    /**
     * If this inventory supports insertion of the given stack.
     */
    public InteractResult canInsert(ItemStack stack){
        ItemScroll.SortPref pref = getHighestPreference(stack);
        return new InteractResult(pref, pref != ItemScroll.SortPref.INVALID);
    }

    /**
     * If this inventory supports extraction of the given stack.
     */
    public InteractResult canExtract(ItemStack stack){
        ItemScroll.SortPref pref = getHighestPreference(stack);
        return new InteractResult(pref, pref != ItemScroll.SortPref.INVALID);
    }

    /**
     * If this inventory supports extraction or insertion of the given stack.
     */
    public InteractResult canInteractFor(ItemStack stack, InteractType type){
        return type == InteractType.EXTRACT ? canExtract(stack) : canInsert(stack);
    }

    /**
     * Returns the highest preference from a list of predicates, unless it is invalid.
     * Invalid overrules all other preferences, as the user does NOT want that item to be inserted.
     */
    public ItemScroll.SortPref getHighestPreference(ItemStack stack){
        ItemScroll.SortPref pref = ItemScroll.SortPref.LOW;
        for(Function<ItemStack, ItemScroll.SortPref> filter : filters){
            ItemScroll.SortPref newPref = filter.apply(stack);
            if(newPref == ItemScroll.SortPref.INVALID){
                return ItemScroll.SortPref.INVALID;
            }else if(newPref.ordinal() > pref.ordinal()){
                pref = newPref;
            }
        }
        return pref;
    }

    public IItemHandler getHandler() {
        return handler;
    }

    public List<Function<ItemStack, ItemScroll.SortPref>> getFilters() {
        return filters;
    }

    public boolean addFilterScroll(ItemStack scrollStack){
        if(scrollStack.getItem() instanceof ItemScroll itemScroll){
            return filters.add(stackIn -> itemScroll.getSortPref(stackIn, scrollStack, handler));
        }
        return false;
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
        if(stack.isEmpty()){
            return stack;
        }
        Set<Integer> slotsForStack = slotCache.getUnchecked(stack.getItem());
        Set<Integer> emptySlots = slotCache.getUnchecked(Items.AIR);
        // go through the inventory and try to fill up already existing items
        for (int i = 0; i < sizeInventory; i++) {
            ItemStack slot = inventory.getStackInSlot(i);
            if (ItemStack.isSameItemSameComponents(slot, stack)) {
                int count = stack.getCount();
                stack = inventory.insertItem(i, stack, simulate);
                if(stack.getCount() != count){
                    slotsForStack.add(i);
                }
                if (stack.isEmpty()) {
                    return stack;
                }
            }else if(slot.isEmpty()){
                emptySlots.add(i);
            }
        }

        List<Integer> invalidSlots = new ArrayList<>();
        // find empty slot
        for (int slot : emptySlots) {
            if (inventory.getStackInSlot(slot).isEmpty()) {
                stack = inventory.insertItem(slot, stack, simulate);
                if (stack.isEmpty()) {
                    break;
                }
            }else{
                invalidSlots.add(slot);
            }
        }

        for(int slot : invalidSlots){
            emptySlots.remove(slot);
        }


        return stack;
    }


    private ItemStack insertItem(ItemStack stack, boolean simulate) {
        IItemHandler dest = handler;
        if (dest == null || stack.isEmpty())
            return stack;

        stack = insertUsingCache(stack, simulate);

        if(stack.isEmpty()){
            return ItemStack.EMPTY;
        }

        for (int i = 0; i < dest.getSlots(); i++) {
            stack = dest.insertItem(i, stack, simulate);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        return stack;
    }

    private ItemStack insertUsingCache(ItemStack stack, boolean simulate){
        IItemHandler dest = handler;
        // Get all empty slots if we can't stack.
        Set<Integer> slots = slotCache.getIfPresent(stack.isStackable() ? stack.getItem() : Items.AIR);
        if(slots == null){
            return stack;
        }

        List<Integer> invalidSlots = new ArrayList<>();
        for(int slot : slots){
            int count = stack.getCount();
            stack = dest.insertItem(slot, stack, simulate);
            if(stack.getCount() == count){
                ItemStack targetStack = dest.getStackInSlot(slot);
                if(!ItemStack.isSameItemSameComponents(targetStack, stack)){
                    invalidSlots.add(slot);
                }
            }else{
                System.out.println("cache hit: " + slot);
            }
        }

        for(int slot : invalidSlots){
            slots.remove(slot);
        }

        return stack;
    }
}
