package com.hollingsworth.arsnouveau.api.item.inv;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public interface IMapInventory {

    ItemStack insertStack(ItemStack stack);

    boolean hasExistingSlotsFor(ItemStack stack);

    default ItemStack getByItem(Item item){
        return getByItem(item, stack -> true);
    }

    ItemStack getByItem(Item item, Predicate<ItemStack> filter);


}
