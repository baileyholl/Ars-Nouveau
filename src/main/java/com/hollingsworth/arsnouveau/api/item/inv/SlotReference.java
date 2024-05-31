package com.hollingsworth.arsnouveau.api.item.inv;

import javax.annotation.Nullable;
import net.neoforged.neoforge.items.IItemHandler;

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

    public static SlotReference empty(){
        return new SlotReference(null, -1);
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
