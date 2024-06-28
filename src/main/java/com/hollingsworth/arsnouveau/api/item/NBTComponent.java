package com.hollingsworth.arsnouveau.api.item;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;

public interface NBTComponent<T> {

    Codec<T> getCodec();

    default Tag toTag(Level level){
        return getCodec().encode((T) this, level.registryAccess().createSerializationContext(NbtOps.INSTANCE), new CompoundTag()).getOrThrow();
    }
}
