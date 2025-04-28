package com.hollingsworth.arsnouveau.api.item.inv;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.items.IItemHandler;

public class HandlerPos {

    public BlockPos pos;
    public BlockCapabilityCache<? extends IItemHandler, Direction> handler;
    public SlotCache slotCache;

    public HandlerPos(BlockPos pos, BlockCapabilityCache<? extends IItemHandler, Direction> handler) {
        this.pos = pos;
        this.handler = handler;
        this.slotCache = new SlotCache();
    }

    public BlockPos pos() {
        return pos;
    }

    public BlockCapabilityCache<? extends IItemHandler, Direction> handler() {
        return handler;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HandlerPos handlerPos) {
            return this.pos.equals(handlerPos.pos);
        }
        return false;
    }
}
