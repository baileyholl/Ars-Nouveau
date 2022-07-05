package com.hollingsworth.arsnouveau.api.nbt;

import net.minecraft.nbt.CompoundTag;

public abstract class AbstractData {

    public AbstractData(CompoundTag tag) {
    }

    public abstract void writeToNBT(CompoundTag tag);

}
