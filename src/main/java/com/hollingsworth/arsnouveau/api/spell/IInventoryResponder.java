package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * An interface used by effects to manipulate inventory blocks.
 * Common implementations include entities or tiles that cast spells and have inventories bound to them in some way.
 */
public interface IInventoryResponder {

    /**
     * @return a list of item handlers that belong to this object.
     */
    @Nonnull
    default List<IItemHandler> getInventory(){return new ArrayList<>();}

    /**
     * @return a specific matching itemstack from the inventories
     */
    @Nonnull
    default ItemStack getItem(ItemStack stack){
        for(IItemHandler i : getInventory()){
            for(int slots = 0; slots < i.getSlots(); slots ++ ){
                if(i.getStackInSlot(slots).sameItem(stack))
                    return i.getStackInSlot(slots);
            }
        }
        return ItemStack.EMPTY;
    }
}
