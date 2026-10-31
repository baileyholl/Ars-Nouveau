package com.hollingsworth.arsnouveau.api.item.inv;

import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntCollections;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.BitSet;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class ImperfectSlotCache implements ISlotCache {
    private Item[] cache;
    private final BitSet empty;
    final Object2IntMap<Item> lastAccepting = new Object2IntOpenHashMap<>();
    private int size;

    public ImperfectSlotCache(int slots) {
        this.cache = new Item[slots];
        this.empty = new BitSet(slots);
        this.size = slots;
        this.lastAccepting.defaultReturnValue(-1);
    }

    public ImperfectSlotCache() {
        this(0);
    }

    @Override
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

        int lastHitSlot = lastAccepting.getInt(item);
        IntArrayList col = new IntArrayList(8);
        if (lastHitSlot >= 0 && lastHitSlot < size && cache[lastHitSlot] == item) {
            col.add(lastHitSlot);
        }
        for (int i = 0; i < size; i++) {
            if (col.size() + emptyCount >= size) {
                break;
            }
            if (i != lastHitSlot && item == cache[i]) {
                col.add(i);
            }
        }

        return IntCollections.unmodifiable(col);
    }

    @Override
    public @Nullable IntCollection getIfPresent(Item item) {
        var slots = this.getOrCreateSlots(item);
        return slots.isEmpty() && empty.isEmpty() ? null : slots;
    }

    @Override
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

    @Override
    public ItemStack insertItemStacked(IItemHandler inventory, ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return stack;

        // not stackable -> just insert into a new slot
        if (!stack.isStackable()) {
            return insertItem(inventory, stack, simulate);
        }

        int sizeInventory = inventory.getSlots();
        Item item = stack.getItem();
        BitSet examined = new BitSet(sizeInventory);
        stack = insertUsingCache(inventory, stack, simulate, examined);
        if (stack.isEmpty()) {
            return stack;
        }
        // Iterate all slots until our stack is empty, caching along the way
        for (int i = 0; i < sizeInventory; i++) {
            // Prevents simulated slots from getting counted twice since the cache was updated above but not the actual contents.
            if (simulate && examined.get(i)) {
                continue;
            }
            ItemStack slot = inventory.getStackInSlot(i);
            if (slot.isEmpty()) {
                this.replaceSlotWithItem(slot.getItem(), i);
            } else {
                int count = stack.getCount();
                stack = inventory.insertItem(i, stack, simulate);
                if (stack.getCount() != count) {
                    this.replaceSlotWithItem(inventory.getStackInSlot(i).getItem(), i);
                    this.lastAccepting.put(item, i);
                }
                if (stack.isEmpty()) {
                    return stack;
                }
            }
        }

        // If we have exhausted inserting
        for (int slot : this.getOrCreateSlots(Items.AIR)) {
            if (slot >= sizeInventory) {
                continue;
            }
            var slotStack = inventory.getStackInSlot(slot);
            if (slotStack.isEmpty()) {
                int count = stack.getCount();
                stack = inventory.insertItem(slot, stack, simulate);
                this.replaceSlotWithItem(inventory.getStackInSlot(slot).getItem(), slot);
                if (stack.getCount() != count) {
                    this.lastAccepting.put(item, slot);
                }
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
        if (stack.isEmpty())
            return stack;

        BitSet examined = new BitSet(dest.getSlots());
        stack = insertUsingCache(dest, stack, simulate, examined);
        if (stack.isEmpty()) {
            return stack;
        }

        stack = insertInCachedEmptySlots(dest, stack, simulate, examined);

        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        Item item = stack.getItem();
        // Iterate all slots until our stack is empty, caching along the way
        for (int i = 0; i < dest.getSlots(); i++) {
            if (simulate && examined.get(i)) {
                continue;
            }
            ItemStack targetStack = dest.getStackInSlot(i);
            int count = stack.getCount();
            stack = dest.insertItem(i, stack, simulate);
            if (stack.getCount() != count) {
                this.replaceSlotWithItem(item.asItem(), i);
            }

            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
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
    private ItemStack insertUsingCache(IItemHandler dest, ItemStack stack, boolean simulate, BitSet examined) {
        Item item = stack.getItem();
        var slots = this.getIfPresent(item);

        if (slots == null || stack.isEmpty()) {
            return stack;
        }

        boolean stackIsStackable = stack.isStackable();
        IntList invalidSlots = new IntArrayList();
        int maxSlots = dest.getSlots();
        for (int slot : slots) {
            if (slot >= maxSlots) {
                invalidSlots.add(slot);
                continue;
            }

            examined.set(slot);
            int count = stack.getCount();
            ItemStack targetStack = dest.getStackInSlot(slot);
            // If this stack wants to stack and our cached slot is air but the slot is not, this means our item has moved locations.
            // If we blindly insert here, we can create partial stacks instead of combining.
            // If this is a non-stackable, ignore this and insert anywhere because our slot list is air.
            if (stackIsStackable && targetStack.isEmpty()) {
                invalidSlots.add(slot);
                continue;
            }

            stack = dest.insertItem(slot, stack, simulate);
            if (stack.getCount() == count) {
                if (!ItemStack.isSameItemSameComponents(targetStack, stack)) {
                    invalidSlots.add(slot);
                }
            } else if (stackIsStackable) {
                this.lastAccepting.put(item, slot);
            }

            if (stack.isEmpty()) {
                break;
            }
        }

        for (int slot : invalidSlots) {
            Item current = slot < maxSlots ? dest.getStackInSlot(slot).getItem() : Items.AIR;
            this.replaceSlotWithItem(current, slot);
        }

        return stack;
    }

    private ItemStack insertInCachedEmptySlots(IItemHandler dest, ItemStack stack, boolean simulate, BitSet examined) {
        var slots = this.getIfPresent(Items.AIR);
        if (slots == null) {
            return stack;
        }

        IntList invalidSlots = new IntArrayList();
        int maxSlots = dest.getSlots();
        for (int slot : slots) {
            if (slot >= maxSlots) {
                invalidSlots.add(slot);
                continue;
            }
            examined.set(slot);
            stack = dest.insertItem(slot, stack, simulate);
            this.replaceSlotWithItem(dest.getStackInSlot(slot).getItem(), slot);
            if (stack.isEmpty()) {
                break;
            }
        }

        for (int slot : invalidSlots) {
            this.replaceSlotWithItem(Items.AIR, slot);
        }

        return stack;
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

    @Override
    public boolean isEmpty(int slot) {
        return this.empty.get(slot);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public int emptyCount() {
        return this.empty.cardinality();
    }

    @Override
    public String toString() {
        return "SlotCache{" +
                "cache=" + Arrays.toString(cache) +
                '}';
    }
}
