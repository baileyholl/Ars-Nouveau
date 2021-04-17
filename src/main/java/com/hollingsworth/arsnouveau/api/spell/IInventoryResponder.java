package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public interface IInventoryResponder {

    @Nonnull
    default List<IItemHandler> getInventory(){return new ArrayList<>();}

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
