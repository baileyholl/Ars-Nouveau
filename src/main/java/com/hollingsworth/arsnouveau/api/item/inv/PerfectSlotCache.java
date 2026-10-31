package com.hollingsworth.arsnouveau.api.item.inv;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntCollections;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PerfectSlotCache implements ISlotCache {
    Object2ObjectOpenHashMap<Item, IntArrayList> cache;
    Item[] slots;

    public PerfectSlotCache(int size, Item[] slots) {
        if (slots.length != size) {
            throw new IllegalArgumentException("length of slots must be equal to size");
        }

        this.cache = new Object2ObjectOpenHashMap<>(size);
        for (int i = 0; i < size; i++) {
            this.cache.computeIfAbsent(slots[i], ignored -> new IntArrayList()).add(i);
        }
        this.slots = slots;
    }

    @Override
    public IntCollection getOrCreateSlots(Item item) {
        return IntCollections.unmodifiable(cache.computeIfAbsent(item, ignored -> new IntArrayList()));
    }

    @Override
    public @Nullable IntCollection getIfPresent(Item item) {
        var list = cache.get(item);
        if (list == null) {
            return null;
        }

        return IntCollections.unmodifiable(list);
    }

    @Override
    public void replaceSlotWithItem(Item newItem, int slot) {
        var oldItem = slots[slot];
        if (oldItem == newItem) {
            // Remove fullness status of slot
            var oldList = cache.get(oldItem);
            if (oldList != null) {
                for (int i = 0 ; i < oldList.size(); i++) {
                    var next = oldList.getInt(i);
                    if (next == ~slot) {
                        oldList.set(i, slot);
                        break;
                    }
                }
            }

            return;
        }

        var oldList = cache.get(oldItem);
        if (oldList != null) {
            for (var iter = oldList.iterator(); iter.hasNext();) {
                var next = iter.nextInt();
                if (next == slot || next == ~slot) {
                    iter.remove();
                    break;
                }
            }
        }

        var list = cache.computeIfAbsent(newItem, ignored -> new IntArrayList());
        list.add(slot);
        list.unstableSort(null);
        slots[slot] = newItem;
    }

    @Override
    public ItemStack insertItemStacked(IItemHandler inventory, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return stack;
        }

        var lastCount = stack.getCount();
        var item = stack.getItem();

        var availableSlots = cache.get(item);
        if (availableSlots != null) {
            for (int i = 0; i < availableSlots.size(); i++) {
                var slot = availableSlots.getInt(i);
                if (slot < 0) {
                    // Slot is full
                    continue;
                }

                stack = inventory.insertItem(slot, stack, simulate);
                var newCount = stack.getCount();
                if (lastCount != newCount) {
                    lastCount = newCount;
                }

                if (stack.isEmpty()) {
                    return stack;
                }

                // Bitwise NOT to indicate fullness
                availableSlots.set(i, ~slot);
            }
        }

        var emptySlots = cache.get(Items.AIR);
        if (emptySlots == null) {
            return stack;
        }

        for (var iter = emptySlots.iterator(); iter.hasNext();) {
            var slot = iter.nextInt();
            stack = inventory.insertItem(slot, stack, simulate);
            if (stack.isEmpty()) {
                return stack;
            }
        }

        return stack;
    }

    @Override
    public boolean isEmpty(int slot) {
        return slots[slot] == Items.AIR;
    }

    @Override
    public int size() {
        return slots.length;
    }

    @Override
    public int emptyCount() {
        var list = cache.get(Items.AIR);
        if (list == null) {
            return 0;
        }

        return list.size();
    }
}
