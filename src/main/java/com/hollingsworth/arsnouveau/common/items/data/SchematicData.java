package com.hollingsworth.arsnouveau.common.items.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public record SchematicData(UUID uuid, UUID owner) {
    public static final Codec<SchematicData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("uuid").forGetter(SchematicData::uuid),
            UUIDUtil.CODEC.fieldOf("owner").forGetter(SchematicData::owner)
    ).apply(instance, SchematicData::new));

    public static final StreamCodec<ByteBuf, SchematicData> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            SchematicData::uuid,
            UUIDUtil.STREAM_CODEC,
            SchematicData::owner,
            SchematicData::new
    );
}
