package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface IWrappedCaster{


    default ItemStack getHeldStack(InteractionHand hand){
        return ItemStack.EMPTY;
    }

//    /**
//     * Returns a reference to the particular stack and its location.
//     */
//    default SlotReference hasStack(Predicate<ItemStack> predicate){
//        for(IItemHandler i : getInventory()){
//            for(int slots = 0; slots < i.getSlots(); slots++){
//                if(predicate.test(i.getStackInSlot(slots)))
//                    return new SlotReference(i, slots);
//            }
//        }
//        return new SlotReference(null, -1);
//    }
//
//    default ExtractedStack extractItem(Predicate<ItemStack> predicate, int count){
//        for(IItemHandler i : getInventory()){
//            ExtractedStack stack = extractItem(i, predicate, count);
//            if(!stack.isEmpty())
//                return stack;
//        }
//        return new ExtractedStack(ItemStack.EMPTY, null, -1);
//    }
//
//    default ExtractedStack extractItem(IItemHandler handler, Predicate<ItemStack> stack, int count){
//        for(int slots = 0; slots < handler.getSlots(); slots++){
//            if(stack.test(handler.getStackInSlot(slots))){
//                return new ExtractedStack(handler.extractItem(slots, count, false), handler, slots);
//            }
//        }
//        return new ExtractedStack(ItemStack.EMPTY, null, -1);
//    }

    @NotNull
    default List<IItemHandler> getInventory() {
        return new ArrayList<>();
    }
}
