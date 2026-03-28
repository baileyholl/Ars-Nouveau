package com.hollingsworth.arsnouveau.api.ritual;


import com.hollingsworth.arsnouveau.api.ritual.features.IBlockPosProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public class ManhattenTracker implements IBlockPosProvider {
    public int i;
    public int j;
    public int k;
    public int l;
    public int pXSize;
    public int pYSize;
    public int pZSize;
    public boolean done;

    public final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
    public int currentDepth;
    public int maxX;
    public int maxY;
    public int x;
    public int y;
    public boolean zMirror;

    public ManhattenTracker(BlockPos pPos, int pXSize, int pYSize, int pZSize) {
        this.i = pXSize + pYSize + pZSize;
        this.j = pPos.getX();
        this.k = pPos.getY();
        this.l = pPos.getZ();
        this.pXSize = pXSize;
        this.pYSize = pYSize;
        this.pZSize = pZSize;
    }

    public CompoundTag serialize(CompoundTag tag) {
        tag.putInt("i", i);
        tag.putInt("j", j);
        tag.putInt("k", k);
        tag.putInt("l", l);
        tag.putInt("pXSize", pXSize);
        tag.putInt("pYSize", pYSize);
        tag.putInt("pZSize", pZSize);
        tag.putBoolean("done", done);
        tag.putInt("currentDepth", currentDepth);
        tag.putInt("maxX", maxX);
        tag.putInt("maxY", maxY);
        tag.putInt("x", x);
        tag.putInt("y", y);
        tag.putBoolean("zMirror", zMirror);
        tag.putInt("cursorX", cursor.getX());
        tag.putInt("cursorY", cursor.getY());
        tag.putInt("cursorZ", cursor.getZ());
        return tag;
    }

    public ManhattenTracker(CompoundTag tag) {
        // 1.21.11: CompoundTag.getInt/getBoolean return Optional; use getIntOr/getBooleanOr
        this.i = tag.getIntOr("i", 0);
        this.j = tag.getIntOr("j", 0);
        this.k = tag.getIntOr("k", 0);
        this.l = tag.getIntOr("l", 0);
        this.pXSize = tag.getIntOr("pXSize", 0);
        this.pYSize = tag.getIntOr("pYSize", 0);
        this.pZSize = tag.getIntOr("pZSize", 0);
        this.done = tag.getBooleanOr("done", false);
        this.currentDepth = tag.getIntOr("currentDepth", 0);
        this.maxX = tag.getIntOr("maxX", 0);
        this.maxY = tag.getIntOr("maxY", 0);
        this.x = tag.getIntOr("x", 0);
        this.y = tag.getIntOr("y", 0);
        this.zMirror = tag.getBooleanOr("zMirror", false);
        this.cursor.set(tag.getIntOr("cursorX", 0), tag.getIntOr("cursorY", 0), tag.getIntOr("cursorZ", 0));
    }

    public BlockPos computeNext() {
        if (done)
            return null;
        if (this.zMirror) {
            this.zMirror = false;
            this.cursor.setZ(l - (this.cursor.getZ() - l));
            return this.cursor;
        } else {
            BlockPos blockpos;
            for (blockpos = null; blockpos == null; ++this.y) {
                if (this.y > this.maxY) {
                    ++this.x;
                    if (this.x > this.maxX) {
                        ++this.currentDepth;
                        if (this.currentDepth > i) {
                            this.done = true;
                            return null;
                        }

                        this.maxX = Math.min(pXSize, this.currentDepth);
                        this.x = -this.maxX;
                    }

                    this.maxY = Math.min(pYSize, this.currentDepth - Math.abs(this.x));
                    this.y = -this.maxY;
                }

                int i1 = this.x;
                int j1 = this.y;
                int k1 = this.currentDepth - Math.abs(i1) - Math.abs(j1);
                if (k1 <= pZSize) {
                    this.zMirror = k1 != 0;
                    blockpos = this.cursor.set(j + i1, k + j1, l + k1);
                }
            }

            return blockpos;
        }
    }
}
