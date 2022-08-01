package com.hollingsworth.arsnouveau.api.item;

import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

/**
 * References a slot in an inventory.
 */
public class SlotReference {
    protected IItemHandler handler;
    protected int slot;

    public SlotReference(IItemHandler handler, int slot){
        this.handler = handler;
        this.slot = slot;
    }

    public boolean isEmpty(){
        return handler == null || slot < 0;
    }

    public @Nullable IItemHandler getHandler(){
        return handler;
    }

    public int getSlot(){
        return slot;
    }
}
