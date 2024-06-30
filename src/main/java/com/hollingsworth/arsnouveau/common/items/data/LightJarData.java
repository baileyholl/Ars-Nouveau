package com.hollingsworth.arsnouveau.common.items.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record LightJarData(BlockPos pos, boolean enabled) {
    public static Codec<LightJarData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(LightJarData::pos),
            Codec.BOOL.fieldOf("enabled").forGetter(LightJarData::enabled)
    ).apply(instance, LightJarData::new));

    public static StreamCodec<RegistryFriendlyByteBuf, LightJarData> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, LightJarData::pos, ByteBufCodecs.BOOL,LightJarData::enabled, LightJarData::new);
}
