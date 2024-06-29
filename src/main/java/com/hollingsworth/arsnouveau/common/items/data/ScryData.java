package com.hollingsworth.arsnouveau.common.items.data;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ScryData(BlockPos pos) {
    public static Codec<ScryData> CODEC = BlockPos.CODEC.xmap(ScryData::new, ScryData::pos);

    public static StreamCodec<RegistryFriendlyByteBuf, ScryData> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, ScryData::pos, ScryData::new);
}
