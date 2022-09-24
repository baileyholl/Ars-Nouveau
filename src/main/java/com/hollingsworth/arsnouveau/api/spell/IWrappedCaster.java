package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.item.ExtractedStack;
import com.hollingsworth.arsnouveau.api.item.FilterableItemHandler;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface IWrappedCaster{

    /**
     * Extracts a stack from the highest preferred inventory that contains items that match the predicate.
     * @param predicate The predicate to match items against.
     */
    default ExtractedStack extractItem(Predicate<ItemStack> predicate, int count){
        ItemScroll.SortPref highestPref = ItemScroll.SortPref.INVALID;
        FilterableItemHandler highestHandler = null;
        for(FilterableItemHandler wrapper : getInventory()){
            ItemScroll.SortPref pref = ItemScroll.SortPref.LOW;
            // Get the highest pref item in the handler
            for(int i = 0; i < wrapper.getHandler().getSlots(); i++){
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
                    return extractItem(wrapper, predicate, count);
            }
            if(pref.ordinal() > highestPref.ordinal()){
                highestHandler = wrapper;
                highestPref = pref;
            }
        }
        if(highestHandler == null){
            return new ExtractedStack(ItemStack.EMPTY, null, -1);
        }
        return extractItem(highestHandler, predicate, count);
    }

    default ExtractedStack extractItem(FilterableItemHandler filteredHandler, Predicate<ItemStack> stack, int count){
        for(int slot = 0; slot < filteredHandler.getHandler().getSlots(); slot++){
            ItemStack stackInSlot = filteredHandler.getHandler().getStackInSlot(slot);
            if(stack.test(stackInSlot) && filteredHandler.canExtract(stackInSlot)){
                return new ExtractedStack(filteredHandler.getHandler().extractItem(slot, count, false), filteredHandler.getHandler(), slot);
            }
        }
        return new ExtractedStack(ItemStack.EMPTY, null, -1);
    }

    default List<FilterableItemHandler> getPreferredInventories(ItemStack stack){
        List<FilterableItemHandler> filtered = new ArrayList<>(getInventory());
        filtered = filtered.stream().filter(filterableItemHandler -> filterableItemHandler.getHighestPreference(stack) != ItemScroll.SortPref.INVALID).collect(Collectors.toCollection(ArrayList::new));
        /// Sort highest pref first
        filtered.sort((o1, o2) -> o2.getHighestPreference(stack).ordinal() - o1.getHighestPreference(stack).ordinal());
        return filtered;
    }

    @NotNull
    default List<FilterableItemHandler> getInventory() {
        return new ArrayList<>();
    }

}
