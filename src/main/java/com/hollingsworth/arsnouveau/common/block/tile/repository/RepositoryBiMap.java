package com.hollingsworth.arsnouveau.common.block.tile.repository;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

public class RepositoryBiMap {
    public Map<Item, IntRBTreeSet> itemToSlot;
    public Map<Integer, Item> slotToItem;

    public RepositoryBiMap(){
        itemToSlot = new HashMap<>();
        slotToItem = new HashMap<>();
    }

    public void put(Item item, int slot){
        itemToSlot.computeIfAbsent(item, k -> new IntRBTreeSet()).add(slot);
        slotToItem.put(slot, item);
    }

    public void remove(Item item, int slot){
        IntRBTreeSet slots = itemToSlot.get(item);
        if (slots != null) {
            slots.remove(slot);
            if (slots.isEmpty()) {
                itemToSlot.remove(item);
            }
        }
    }



}
