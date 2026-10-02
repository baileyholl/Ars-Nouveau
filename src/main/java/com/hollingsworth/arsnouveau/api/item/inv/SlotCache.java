package com.hollingsworth.arsnouveau.api.item.inv;

import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntCollections;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.BitSet;

public final class SlotCache {
    private Item[] cache;
    private final BitSet empty;
    private int size;
    // Used for optimizations when the cache is perfectly maintained
    public boolean perfect;

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

    public void replaceSlotWithItem(Item newItem, int slot) {
        this.ensureSlot(slot);
        cache[slot] = newItem;
        empty.set(slot, newItem == Items.AIR);
    }

    public void initEmpty(int slot) {
        this.ensureSlot(slot);
        cache[slot] = Items.AIR;
        empty.set(slot);
    }

    public void ensureSlot(int slot) {
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

    /**
     * Inserts the ItemStack into an inventory, filling up already present stacks first.
     * This is equivalent to the behaviour of a player picking up an item.
     * Note: This function stacks items without subtypes with different metadata together.
     */
    public ItemStack insertItemStacked(IItemHandler dest, ItemStack stack, boolean simulate) {
        if (dest == null || stack.isEmpty())
            return stack;

        // not stackable -> just insert into a new slot
        if (!stack.isStackable()) {
            return insertItem(dest, stack, simulate);
        }

        int sizeInventory = dest.getSlots();
        stack = this.insertUsingCache(dest, stack, simulate);
        if (stack.isEmpty()) {
            return stack;
        }

        stack = this.insertInCachedEmptySlots(dest, stack, simulate);
        if (perfect || stack.isEmpty()) {
            return stack;
        }

        // Iterate all slots until our stack is empty, caching along the way
        for (int i = 0; i < sizeInventory; i++) {
            ItemStack slot = dest.getStackInSlot(i);
            if (slot.isEmpty()) {
                this.replaceSlotWithItem(slot.getItem(), i);
            } else {
                int count = stack.getCount();
                stack = dest.insertItem(i, stack, simulate);
                if (stack.getCount() != count) {
                    this.replaceSlotWithItem(dest.getStackInSlot(i).getItem(), i);
                }
                if (stack.isEmpty()) {
                    return stack;
                }
            }
        }

        // If we have exhausted inserting
        for (int slot : this.getOrCreateSlots(Items.AIR)) {
            var slotStack = dest.getStackInSlot(slot);
            if (slotStack.isEmpty()) {
                stack = dest.insertItem(slot, stack, simulate);
                this.replaceSlotWithItem(dest.getStackInSlot(slot).getItem(), slot);
                if (stack.isEmpty()) {
                    break;
                }
            } else {
                this.replaceSlotWithItem(slotStack.getItem(), slot);
            }
        }

        return stack;
    }

    /**
     * Inserts into any valid cached slots, then inserts into any empty cached slots.
     * If no cached slots are available, it will insert into any empty slots.
     * Optimizes non-stackable item insertion.
     */
    private ItemStack insertItem(IItemHandler dest, ItemStack stack, boolean simulate) {
        if (dest == null || stack.isEmpty())
            return stack;

        stack = this.insertUsingCache(dest, stack, simulate);
        if (stack.isEmpty()) {
            return stack;
        }

        stack = this.insertInCachedEmptySlots(dest, stack, simulate);
        if (perfect || stack.isEmpty()) {
            return stack;
        }

        Item item = stack.getItem();
        // Iterate all slots until our stack is empty, caching along the way
        for (int i = 0; i < dest.getSlots(); i++) {
            ItemStack targetStack = dest.getStackInSlot(i);
            int count = stack.getCount();
            stack = dest.insertItem(i, stack, simulate);
            if (stack.getCount() != count) {
                this.replaceSlotWithItem(item.asItem(), i);
            }

            if (stack.isEmpty()) {
                return stack;
            }

            if (targetStack.isEmpty()) {
                this.initEmpty(i);
            }
        }

        return stack;
    }

    /**
     * Inserts into cached slots if any and invalidates slots.
     * If there are no cached slots, this does nothing.
     */
    private ItemStack insertUsingCache(IItemHandler dest, ItemStack stack, boolean simulate) {
        var slots = this.getIfPresent(stack.getItem());

        if (slots == null || stack.isEmpty()) {
            return stack;
        }

        boolean stackIsStackable = stack.isStackable();
        var invalidSlots = new IntArrayList();
        int maxSlots = dest.getSlots();
        for (int slot : slots) {
            if (slot >= maxSlots) {
                invalidSlots.add(slot);
                continue;
            }

            int count = stack.getCount();
            ItemStack targetStack = dest.getStackInSlot(slot);
            // If this stack wants to stack and our cached slot is air but the slot is not, this means our item has moved locations.
            // If we blindly insert here, we can create partial stacks instead of combining.
            // If this is a non-stackable, ignore this and insert anywhere because our slot list is air.
            if (!perfect && stackIsStackable && targetStack.isEmpty()) {
                invalidSlots.add(slot);
                continue;
            }

            stack = dest.insertItem(slot, stack, simulate);
            if (stack.getCount() == count) {
                if (!perfect && !ItemStack.isSameItem(targetStack, stack)) {
                    invalidSlots.add(slot);
                }
            }

            if (stack.isEmpty()) {
                break;
            }
        }

        if (!perfect) {
            for (int slot : invalidSlots) {
                Item current = slot < maxSlots ? dest.getStackInSlot(slot).getItem() : Items.AIR;
                this.replaceSlotWithItem(current, slot);
            }
        }

        return stack;
    }

    private ItemStack insertInCachedEmptySlots(IItemHandler dest, ItemStack stack, boolean simulate) {
        var slots = this.getIfPresent(Items.AIR);
        if (slots == null) {
            return stack;
        }

        int maxSlots = dest.getSlots();
        for (int slot : slots) {
            if (slot >= maxSlots) {
                break;
            }

            stack = dest.insertItem(slot, stack, simulate);
            this.replaceSlotWithItem(dest.getStackInSlot(slot).getItem(), slot);
            if (stack.isEmpty()) {
                break;
            }
        }

        return stack;
    }

    @Override
    public String toString() {
        return "SlotCache{" +
                "cache=" + cache +
                '}';
    }
}
