package com.hollingsworth.arsnouveau.api.nbt;

import net.minecraft.nbt.CompoundTag;

@Deprecated(forRemoval = true)
public interface ITagSerializable {

    CompoundTag serialize();

}
