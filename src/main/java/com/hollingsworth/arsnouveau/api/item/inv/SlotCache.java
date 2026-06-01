package com.hollingsworth.arsnouveau.api.item.inv;

import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntCollections;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.BitSet;

public final class SlotCache {
    private Item[] cache;
    private final BitSet empty;
    private int size;

    public SlotCache(int slots) {
        this.cache = new Item[slots];
        this.empty = new BitSet(slots);
        this.size = slots;
    }

    public SlotCache() {
        this(0);
    }

    public IntCollection getOrCreateSlots(Item item) {
        var emptyCount = this.emptyCount();
        if (item == Items.AIR) {
            IntArrayList col = new IntArrayList(emptyCount);
            for (int i = empty.nextSetBit(0); col.size() < emptyCount; i = empty.nextSetBit(i + 1)) {
                col.add(i);
            }

            return IntCollections.unmodifiable(col);
        }

        if (emptyCount >= size) {
            return IntList.of();
        }

        IntArrayList col = new IntArrayList(8);
        for (int i = 0; i < size; i++) {
            if (col.size() + emptyCount >= size) {
                break;
            }
            if (item == cache[i]) {
                col.add(i);
            }
        }

        return IntCollections.unmodifiable(col);
    }

    public @Nullable IntCollection getIfPresent(Item item) {
        var slots = this.getOrCreateSlots(item);
        return slots.isEmpty() && empty.isEmpty() ? null : slots;
    }

    public void replaceSlotWithItem(Item extracted, Item newItem, int slot) {
        this.ensureSlot(slot);

        if (extracted == Items.AIR) {
            empty.clear(slot);
        }

        cache[slot] = newItem;
        if (newItem == Items.AIR) {
            empty.set(slot);
        }
    }

    public void initEmpty(int slot) {
        this.ensureSlot(slot);
        cache[slot] = Items.AIR;
        empty.set(slot);
    }

    private void ensureSlot(int slot) {
        var capacity = IntMath.ceilingPowerOfTwo(Math.max(4, slot + 1));
        if (cache.length < capacity) {
            var bigger = new Item[capacity];
            System.arraycopy(cache, 0, bigger, 0, size);
            for (int i = size; i < capacity; i++) {
                bigger[i] = Items.AIR;
            }

            cache = bigger;
        }
        size = Math.max(size, slot + 1);
    }

    public boolean isEmpty(int slot) {
        return this.empty.get(slot);
    }

    public int size() {
        return this.size;
    }

    public int emptyCount() {
        return this.empty.cardinality();
    }

    @Override
    public String toString() {
        return "SlotCache{" +
                "cache=" + cache +
                '}';
    }
}
