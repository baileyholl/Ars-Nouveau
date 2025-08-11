package com.hollingsworth.arsnouveau.api.util;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class BlockPosSet implements Set<BlockPos> {
    private final Set<Long> inner;

    public BlockPosSet(@NotNull Set<Long> inner) {
        this.inner = inner;
    }

    public BlockPosSet(@NotNull Collection<BlockPos> from) {
        this.inner = new LongOpenHashSet();
        this.addAll(from);
    }

    public static @NotNull BlockPosSet newHashSet() {
        return new BlockPosSet(new LongOpenHashSet());
    }

    @Override
    public int size() {
        return this.inner.size();
    }

    @Override
    public boolean isEmpty() {
        return this.inner.isEmpty();
    }

    @Deprecated
    @Override
    public boolean contains(Object o) {
        return o instanceof BlockPos pos && this.inner.contains(pos.asLong());
    }

    public boolean contains(long key) {
        return this.inner.contains(key);
    }

    @NotNull
    @Override
    public Iterator<BlockPos> iterator() {
        return new Iterator<>() {
            private final Iterator<Long> iter = inner.iterator();

            @Override
            public boolean hasNext() {
                return this.iter.hasNext();
            }

            @Override
            public BlockPos next() {
                return BlockPos.of(this.iter.next());
            }

            @Override
            public void remove() {
                this.iter.remove();
            }
        };
    }

    @NotNull
    @Override
    public Object @NotNull [] toArray() {
        Object[] array = new Object[this.size()];
        return this.toArray(array);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public <T> T @NotNull [] toArray(@NotNull T @NotNull [] a) {
        int i = 0;
        for (var key : this.inner) {
            a[i++] = (T) BlockPos.of(key);
        }
        return a;
    }

    @Override
    public boolean add(BlockPos blockPos) {
        return this.inner.add(blockPos.asLong());
    }

    @Override
    public boolean remove(Object o) {
        return o instanceof BlockPos pos && this.remove(pos);
    }

    public boolean remove(BlockPos pos) {
        return this.inner.remove(pos.asLong());
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        for (Object o : c) {
            if (!this.contains(o)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends BlockPos> c) {
        var ret = false;
        for (BlockPos pos : c) {
            ret |= this.add(pos);
        }

        return ret;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        boolean ret = false;
        var iter = this.iterator();
        while (iter.hasNext()) {
            var pos = iter.next();
            if (!c.contains(pos)) {
                iter.remove();
                ret = true;
            }
        }

        return ret;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        boolean ret = false;
        var iter = this.iterator();
        while (iter.hasNext()) {
            var pos = iter.next();
            if (c.contains(pos)) {
                iter.remove();
                ret = true;
            }
        }

        return ret;
    }

    @Override
    public void clear() {
        this.inner.clear();
    }
}
