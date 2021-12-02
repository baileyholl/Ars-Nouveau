package com.hollingsworth.arsnouveau.common.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AnimatedTile extends ModdedTile {
    public int counter = 0;

    public AnimatedTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    @Override
    public void load(CompoundTag tag) {
        counter = tag.getInt("counter");
        super.load(tag);
    }


    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putInt("counter", counter);
        return super.save(tag);
    }
}
