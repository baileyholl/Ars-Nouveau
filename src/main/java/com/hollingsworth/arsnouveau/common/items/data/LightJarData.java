package com.hollingsworth.arsnouveau.common.items.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;
import java.util.Optional;

public record LightJarData(Optional<BlockPos> pos, boolean enabled) {
    public static final Codec<LightJarData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.optionalFieldOf("pos").forGetter(LightJarData::pos),
            Codec.BOOL.fieldOf("enabled").forGetter(LightJarData::enabled)
    ).apply(instance, LightJarData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LightJarData> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC.apply(ByteBufCodecs::optional), LightJarData::pos, ByteBufCodecs.BOOL,LightJarData::enabled, LightJarData::new);

    public LightJarData() {
        this(Optional.empty(), false);
    }

    public LightJarData(BlockPos pos, boolean enabled) {
        this(Optional.of(pos), enabled);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LightJarData that = (LightJarData) o;
        return enabled == that.enabled && Objects.equals(pos, that.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, enabled);
    }
}
