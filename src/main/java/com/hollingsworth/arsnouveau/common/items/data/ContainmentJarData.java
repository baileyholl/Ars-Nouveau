package com.hollingsworth.arsnouveau.common.items.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public record ContainmentJarData(CompoundTag entityTag, CompoundTag extraDataTag){
    public static Codec<ContainmentJarData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CompoundTag.CODEC.fieldOf("entityTag").forGetter(ContainmentJarData::entityTag),
            CompoundTag.CODEC.fieldOf("extraDataTag").forGetter(ContainmentJarData::extraDataTag)
    ).apply(instance, ContainmentJarData::new));

    public static StreamCodec<RegistryFriendlyByteBuf, ContainmentJarData> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG, ContainmentJarData::entityTag, ByteBufCodecs.COMPOUND_TAG, ContainmentJarData::extraDataTag, ContainmentJarData::new);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContainmentJarData that = (ContainmentJarData) o;
        return Objects.equals(entityTag, that.entityTag) && Objects.equals(extraDataTag, that.extraDataTag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityTag, extraDataTag);
    }
}
