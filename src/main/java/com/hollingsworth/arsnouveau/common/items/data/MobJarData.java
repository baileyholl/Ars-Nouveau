package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.common.crafting.recipes.CheatSerializer;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public record MobJarData(CompoundTag entityTag) {

    public static final Codec<MobJarData> CODEC = CompoundTag.CODEC.xmap(MobJarData::new, MobJarData::entityTag);
    public static final StreamCodec<RegistryFriendlyByteBuf, MobJarData> STREAM = CheatSerializer.create(CODEC);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MobJarData that = (MobJarData) o;
        return Objects.equals(entityTag, that.entityTag);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entityTag);
    }
}
