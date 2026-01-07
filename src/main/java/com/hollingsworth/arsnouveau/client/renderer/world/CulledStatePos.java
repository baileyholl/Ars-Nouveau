package com.hollingsworth.arsnouveau.client.renderer.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class CulledStatePos {
    public BlockState state;
    public BlockPos pos;
    public CompoundTag tag;
    private boolean[] renderDirections = new boolean[6];
    private boolean skipRender = false;
    private boolean needsUpdate;


    public CulledStatePos(BlockState state, BlockPos pos, CompoundTag tag) {
        this.state = state;
        this.pos = pos;
        this.tag = tag;
        needsUpdate = true;
    }

    public boolean needsUpdate() {
        return needsUpdate;
    }

    public boolean setRenderDirection(Direction direction, boolean value) {
        renderDirections[direction.ordinal()] = value;
        return value;
    }

    public boolean shouldRenderFace(Direction direction) {
        return !skipRender && renderDirections[direction.ordinal()];
    }


    public void setSkipRender(boolean skip) {
        this.skipRender = skip;
    }

    public void setNeedsUpdate(boolean needsUpdate) {
        this.needsUpdate = needsUpdate;
    }

    public boolean shouldSkipRender() {
        return skipRender || state.isEmpty();
    }
}
