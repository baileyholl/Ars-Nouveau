package com.hollingsworth.arsnouveau.api.item;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ExtractedStack extends SlotReference{

    public ItemStack stack;

    public ExtractedStack(ItemStack stack, IItemHandler handler, int slot) {
        super(handler, slot);
        this.stack = stack;
    }

    public ItemStack insertBack(){
        return this.handler.insertItem(slot, stack, false);
    }

    public boolean isEmpty() {
        return super.isEmpty() || stack.isEmpty();
    }

    public ItemStack getStack() {
        return stack;
    }
}
