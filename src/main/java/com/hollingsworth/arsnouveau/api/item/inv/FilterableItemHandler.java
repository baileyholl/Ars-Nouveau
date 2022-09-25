package com.hollingsworth.arsnouveau.api.item.inv;

import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Represents an ItemHandler and its list of filters.
 */
public class FilterableItemHandler {

    private IItemHandler handler;
    private List<Function<ItemStack, ItemScroll.SortPref>> filters;

    public FilterableItemHandler(IItemHandler handler){
        this(handler, new ArrayList<>());
    }

    public FilterableItemHandler(IItemHandler handler, List<Function<ItemStack, ItemScroll.SortPref>> filters){
        this.handler = handler;
        this.filters = filters;
    }

    public boolean canInsert(ItemStack stack){
        return getHighestPreference(stack) != ItemScroll.SortPref.INVALID;
    }

    public boolean canExtract(ItemStack stack){
        return getHighestPreference(stack) != ItemScroll.SortPref.INVALID;
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
}
