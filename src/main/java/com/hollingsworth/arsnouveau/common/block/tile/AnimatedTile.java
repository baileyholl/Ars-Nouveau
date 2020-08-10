package com.hollingsworth.arsnouveau.common.block.tile;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public abstract class AnimatedTile extends TileEntity implements ITickableTileEntity {
    public int counter = 0;
    public AnimatedTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void read(CompoundNBT tag) {
        counter = tag.getInt("counter");
        super.read(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag.putInt("counter", counter);
        return super.write(tag);
    }
}
