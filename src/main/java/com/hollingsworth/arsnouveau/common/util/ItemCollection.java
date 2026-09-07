package com.hollingsworth.arsnouveau.common.util;

import com.hollingsworth.nuggets.common.util.ItemStackKey;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;

public class ItemCollection {
    private final Object2IntMap<ItemStackKey> map;

    public ItemCollection(int size) {
        this(new Object2IntArrayMap<>(size));
    }

    public ItemCollection() {
        this(new Object2IntArrayMap<>());
    }

    public ItemCollection(Object2IntMap<ItemStackKey> map) {
        this.map = map;
    }

    public int add(ItemStack stack) {
        return this.add(stack, stack.getCount());
    }

    public int add(ItemStack stack, int count) {
        if (count == 0) {
            return map.getOrDefault(new ItemStackKey(stack, true), 0);
        }

        //noinspection AutoBoxing,UnnecessaryUnboxing
        return map.computeInt(new ItemStackKey(stack, true), (k, v) -> v == null ? count : v.intValue() + count);
    }

    public Iterator<ItemStack> iterator() {
        var iter = Object2IntMaps.fastIterator(map);
        return new Iterator<>() {
            private final ObjectIterator<Object2IntMap.Entry<ItemStackKey>> inner = iter;
            private Object2IntMap.Entry<ItemStackKey> current;
            private ItemStack stack;
            private int size;
            private int maxStackSize;

            @Override
            public boolean hasNext() {
                return inner.hasNext();
            }

            @Override
            public ItemStack next() {
                if (size > 0) {
                    var stackSize = Math.min(size, maxStackSize);
                    size -= stackSize;
                    return stack.copyWithCount(stackSize);
                }

                current = inner.next();
                size = current.getIntValue();
                stack = current.getKey().getStack();
                maxStackSize = stack.getMaxStackSize();
                var stackSize = Math.min(size, maxStackSize);
                size -= stackSize;
                return stack.copyWithCount(stackSize);
            }
        };
    }
}
