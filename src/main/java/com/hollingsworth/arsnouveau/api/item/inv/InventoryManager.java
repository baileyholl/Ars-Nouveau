package com.hollingsworth.arsnouveau.api.item.inv;

import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.IWrappedCaster;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * Stores a list of {@link FilterableItemHandler}s and provides methods to interact with them.
 *
 */
public class InventoryManager {

    private List<FilterableItemHandler> filterables;

    private boolean hasTransformed;

    private int slotMax = -1;

    public InventoryManager(List<FilterableItemHandler> filterables){
        this.filterables = filterables;
    }

    public InventoryManager(IWrappedCaster wrappedCaster){
        this(wrappedCaster.getInventory());
    }

    public InventoryManager withSlotMax(int slotMax){
        this.slotMax = slotMax;
        return this;
    }

    public List<FilterableItemHandler> getInventory(){
        return filterables;
    }


    public ExtractedStack extractByAmount(ToIntFunction<ItemStack> getExtractAmount){
        ItemScroll.SortPref highestPref = ItemScroll.SortPref.INVALID;
        FilterableItemHandler highestHandler = null;
        int toExtract = 0;
        int slot = -1;
        for(FilterableItemHandler wrapper : getInventory()){
            ItemScroll.SortPref pref = ItemScroll.SortPref.INVALID;
            // Get the highest pref item in the handler
            int forAmount = 0;
            int forSlot = 0;
            for(int i = 0; i < slotsForHandler(wrapper); i++){
                ItemStack stack = wrapper.getHandler().getStackInSlot(i);
                if(stack.isEmpty()) {
                    continue;
                }
                int amount = getExtractAmount.applyAsInt(stack);
                if(amount <= 0)
                    continue;
                ItemScroll.SortPref foundPref = wrapper.getHighestPreference(stack);
                if(pref == ItemScroll.SortPref.HIGHEST) {
                    return extractItem(wrapper, stack1 -> true, amount);
                }else if(foundPref == ItemScroll.SortPref.INVALID){
                    continue;
                }
                if(foundPref.ordinal() > pref.ordinal()){
                    pref = foundPref;
                    forAmount = amount;
                    forSlot = i;
                }
            }
            if(pref.ordinal() > highestPref.ordinal()){
                highestHandler = wrapper;
                highestPref = pref;
                toExtract = forAmount;
                slot = forSlot;
            }
        }
        if(highestHandler == null){
            return new ExtractedStack(ItemStack.EMPTY, null, -1);
        }
        return ExtractedStack.from(highestHandler.getHandler(), slot, toExtract);
    }

    /**
     * Extracts a stack from the highest preferred inventory that contains items that match the predicate.
     * @param predicate The predicate to match items against.
     */
    public ExtractedStack extractItem(Predicate<ItemStack> predicate, int count){
        FilterableItemHandler highestHandler = highestPrefInventory(getInventory(), predicate);
        if(highestHandler == null){
            return new ExtractedStack(ItemStack.EMPTY, null, -1);
        }
        return extractItem(highestHandler, predicate, count);
    }

    public ExtractedStack extractItem(FilterableItemHandler filteredHandler, Predicate<ItemStack> stackPredicate, int count){
        SlotReference slotRef = findItem(filteredHandler, stackPredicate);
        if(slotRef.isEmpty()){
            return new ExtractedStack(ItemStack.EMPTY, null, -1);
        }
        return ExtractedStack.from(slotRef, count);
    }


    public SlotReference findItem(FilterableItemHandler itemHandler, Predicate<ItemStack> stackPredicate){
        for(int slot = 0; slot < slotsForHandler(itemHandler); slot++){
            ItemStack stackInSlot = itemHandler.getHandler().getStackInSlot(slot);
            if(!stackInSlot.isEmpty() && stackPredicate.test(stackInSlot) && itemHandler.canExtract(stackInSlot)){
                return new SlotReference(itemHandler.getHandler(), slot);
            }
        }
        return new SlotReference(null, -1);
    }


    public List<FilterableItemHandler> getPreferredInventories(ItemStack stack){
        List<FilterableItemHandler> filtered = new ArrayList<>(getInventory());
        filtered = filtered.stream().filter(filterableItemHandler -> filterableItemHandler.getHighestPreference(stack) != ItemScroll.SortPref.INVALID).collect(Collectors.toCollection(ArrayList::new));
        /// Sort highest pref first
        filtered.sort((o1, o2) -> o2.getHighestPreference(stack).ordinal() - o1.getHighestPreference(stack).ordinal());
        return filtered;
    }

    private FilterableItemHandler highestPrefInventory(List<FilterableItemHandler> inventories, Predicate<ItemStack> predicate){
        ItemScroll.SortPref highestPref = ItemScroll.SortPref.INVALID;
        FilterableItemHandler highestHandler = null;
        for(FilterableItemHandler wrapper : getInventory()){
            ItemScroll.SortPref pref = ItemScroll.SortPref.LOW;
            // Get the highest pref item in the handler
            for(int i = 0; i < slotsForHandler(wrapper); i++){
                ItemStack stack = wrapper.getHandler().getStackInSlot(i);
                if(stack.isEmpty() || !predicate.test(stack))
                    continue;
                ItemScroll.SortPref foundPref = wrapper.getHighestPreference(stack);
                if(foundPref == ItemScroll.SortPref.INVALID){
                    continue;
                }
                if(foundPref.ordinal() > pref.ordinal()){
                    pref = foundPref;
                }
                if(pref == ItemScroll.SortPref.HIGHEST)
                    return wrapper;
            }
            if(pref.ordinal() > highestPref.ordinal()){
                highestHandler = wrapper;
                highestPref = pref;
            }
        }
        return highestHandler;
    }

    private int slotsForHandler(FilterableItemHandler handler){
        if(slotMax == -1)
            return handler.getHandler().getSlots();
        return Math.min(slotMax, handler.getHandler().getSlots());
    }
}
