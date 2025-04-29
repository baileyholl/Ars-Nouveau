package com.hollingsworth.arsnouveau.api.item.inv;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SlotCache {
    protected LoadingCache<Item, IntRBTreeSet> cache;

    public SlotCache(){
        this(true);
    }

    public SlotCache(boolean shouldExpire){
        if(shouldExpire){
            this.cache = CacheBuilder.newBuilder()
                    .maximumSize(100)
                    .expireAfterAccess(30, TimeUnit.MINUTES)
                    .build(CacheLoader.from((key) -> new IntRBTreeSet()));;
        } else {
            this.cache = CacheBuilder.newBuilder()
                    .maximumSize(100)
                    .build(CacheLoader.from((key) -> new IntRBTreeSet()));;
        }
    }

    public Collection<Integer> getOrCreateSlots(Item item){
        return cache.getUnchecked(item);
    }

    public @Nullable Collection<Integer> getIfPresent(Item item){
        return cache.getIfPresent(item);
    }

    public void replaceSlotWithItem(Item extracted, Item newItem, int slot){
        cache.getUnchecked(extracted).remove(slot);
        cache.getUnchecked(newItem).add(slot);
    }

    public void addSlot(Item item, int slot){
        getOrCreateSlots(item).add(slot);
    }

    public void removeAll(Item item, List<Integer> slotList){
        cache.getUnchecked(item).removeAll(slotList);
        if(cache.getUnchecked(item).isEmpty()){
            cache.invalidate(item);
        }
    }

    public void removeSlot(Item item, int slot){
        Collection<Integer> slotList = cache.getUnchecked(item);
        slotList.remove(slot);
        if(slotList.isEmpty()){
            cache.invalidate(item);
        }
    }

    public Collection<Item> getKeys(){
        return cache.asMap().keySet();
    }

    @Override
    public String toString() {
        return "SlotCache{" +
                "cache=" + cache.asMap() +
                '}';
    }
}
