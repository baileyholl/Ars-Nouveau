package com.hollingsworth.arsnouveau.common.items.data;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ScryPosData(BlockPos pos) {
    public static Codec<ScryPosData> CODEC = BlockPos.CODEC.xmap(ScryPosData::new, ScryPosData::pos);

    public static StreamCodec<RegistryFriendlyByteBuf, ScryPosData> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, ScryPosData::pos, ScryPosData::new);
}
