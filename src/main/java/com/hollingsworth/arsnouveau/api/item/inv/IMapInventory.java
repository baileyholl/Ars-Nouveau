package com.hollingsworth.arsnouveau.api.item.inv;

import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public interface IMapInventory {

    ItemStack insertStack(ItemStack stack);

    boolean hasExistingSlotsForInsertion(ItemStack stack);

    default ItemStack extractByItem(Item item){
        return extractByItem(item, stack -> true);
    }

    ItemStack extractByItem(Item item, Predicate<ItemStack> filter);

    ItemScroll.SortPref getInsertionPreference(ItemStack stack);


}
