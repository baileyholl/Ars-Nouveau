package com.hollingsworth.arsnouveau.api.nbt;

import net.minecraft.nbt.CompoundTag;

public abstract class AbstractData {

    public AbstractData(CompoundTag tag) {
        readFromNBT(tag);
    }

    public abstract void writeToNBT(CompoundTag tag);

    public abstract void readFromNBT(CompoundTag tag);

}
