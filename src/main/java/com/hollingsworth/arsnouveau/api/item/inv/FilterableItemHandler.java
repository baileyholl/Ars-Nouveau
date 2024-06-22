package com.hollingsworth.arsnouveau.api.item.inv;

import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents an ItemHandler and its list of filters.
 */
public class FilterableItemHandler {

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

}
