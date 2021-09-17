package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * An interface used by effects to manipulate inventory blocks.
 * Common implementations include entities or tiles that cast spells and have inventories bound to them in some way.
 */
public interface IInventoryResponder {

    /**
     * @return a list of item handlers that belong to this object.
     */
    @Nonnull default List<IItemHandler> getInventory(){return new ArrayList<>();}

    /**
     * @return a specific matching itemstack from the inventories. DO NOT MODIFY. USE EXTRACT
     */
    @Nonnull default ItemStack getItem(ItemStack stack){
        return getItem((i) -> i.sameItem(stack));
    }

    @Nonnull default ItemStack getItem(Predicate<ItemStack> predicate){
        for(IItemHandler i : getInventory()){
            for(int slots = 0; slots < i.getSlots(); slots ++ ){
                if(predicate.test(i.getStackInSlot(slots)))
                    return i.getStackInSlot(slots);
            }
        }
        return ItemStack.EMPTY;
    }

    @Nonnull default ItemStack extractItem(Predicate<ItemStack> predicate, int count){
        for(IItemHandler i : getInventory()){
            for(int slots = 0; slots < i.getSlots(); slots ++ ){
                if(predicate.test(i.getStackInSlot(slots)))
                    return i.extractItem(slots, count, false);
            }
        }
        return ItemStack.EMPTY;
    }
}
