package com.hollingsworth.arsnouveau.common.items.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;
import java.util.Optional;

public record MobJarData(Optional<CompoundTag> entityTag, Optional<CompoundTag> extraDataTag){
    public static Codec<MobJarData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CompoundTag.CODEC.optionalFieldOf("entity_tag").forGetter(MobJarData::entityTag),
            CompoundTag.CODEC.optionalFieldOf("extra_data_tag").forGetter(MobJarData::extraDataTag)
    ).apply(instance, MobJarData::new));

    public MobJarData(CompoundTag tag, CompoundTag extraTag){
        this(Optional.ofNullable(tag), Optional.ofNullable(extraTag));
    }

    public static StreamCodec<RegistryFriendlyByteBuf, MobJarData> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.OPTIONAL_COMPOUND_TAG, MobJarData::entityTag, ByteBufCodecs.OPTIONAL_COMPOUND_TAG, MobJarData::extraDataTag, MobJarData::new);


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MobJarData that = (MobJarData) o;
        return Objects.equals(entityTag, that.entityTag) && Objects.equals(extraDataTag, that.extraDataTag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityTag, extraDataTag);
    }
}
