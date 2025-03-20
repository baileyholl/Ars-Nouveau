package com.hollingsworth.arsnouveau.api.item.inv;

import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FilterSet {
    public List<Function<ItemStack, ItemScroll.SortPref>> filters;

    public FilterSet(List<Function<ItemStack, ItemScroll.SortPref>> filters){
        this.filters = filters;
    }

    public FilterSet(){
        this.filters = new ArrayList<>();
    }

    public static FilterSet forPosition(Level level, BlockPos pos){
        List<Function<ItemStack, ItemScroll.SortPref>> filters = new ArrayList<>();
        IItemHandler inv = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
        if(inv == null){
            return new FilterSet(filters);
        }

        for (ItemFrame i : level.getEntitiesOfClass(ItemFrame.class, new AABB(pos).inflate(1))) {
            BlockPos attachedTo = i.blockPosition().relative(i.getDirection().getOpposite());
            if(!attachedTo.equals(pos)){
                continue;
            }
            ItemStack stackInFrame = i.getItem();
            if (i.getItem().isEmpty() || stackInFrame.isEmpty()) {
                continue;
            }

            if (stackInFrame.getItem() instanceof ItemScroll scrollItem) {
                filters.add(stackToStore -> scrollItem.getSortPref(stackToStore, stackInFrame, inv));
            } else {
                filters.add(stackToStore -> stackToStore.getItem() == stackInFrame.getItem() ? ItemScroll.SortPref.HIGHEST : ItemScroll.SortPref.INVALID);
            }
        }

        return new FilterSet(filters);
    }

    public boolean addFilterScroll(ItemStack scrollStack, IItemHandler itemHandler){
        if(scrollStack.getItem() instanceof ItemScroll itemScroll){
            return filters.add(stackIn -> itemScroll.getSortPref(stackIn, scrollStack, itemHandler));
        }
        return false;
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
}
