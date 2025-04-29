package com.hollingsworth.arsnouveau.api.item.inv;

import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

/**
 * References a slot in an inventory.
 */
public class SlotReference {
    protected WeakReference<IItemHandler> handler;
    protected int slot;

    public SlotReference(IItemHandler handler, int slot){
        this.handler = new WeakReference<>(handler);
        this.slot = slot;
    }

    public static SlotReference empty(){
        return new SlotReference(null, -1);
    }

    public boolean isEmpty(){
        return handler == null || slot < 0;
    }

    public @Nullable IItemHandler getHandler(){
        return handler == null ? null : handler.get();
    }

    public int getSlot(){
        return slot;
    }
}
