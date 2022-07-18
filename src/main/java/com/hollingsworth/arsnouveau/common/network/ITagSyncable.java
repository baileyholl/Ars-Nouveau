package com.hollingsworth.arsnouveau.common.network;

import net.minecraft.nbt.CompoundTag;

public interface ITagSyncable {

    void onTagSync(CompoundTag tag);

}
