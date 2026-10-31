package com.hollingsworth.arsnouveau.common.items.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.Vec3i;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;
import java.util.UUID;

public record SchematicData(UUID uuid, UUID owner, Optional<Vec3i> size) {
    private static final StreamCodec<ByteBuf, Vec3i> SIZE_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            Vec3i::getX,
            ByteBufCodecs.VAR_INT,
            Vec3i::getY,
            ByteBufCodecs.VAR_INT,
            Vec3i::getZ,
            Vec3i::new
    );

    public static final Codec<SchematicData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("uuid").forGetter(SchematicData::uuid),
            UUIDUtil.CODEC.fieldOf("owner").forGetter(SchematicData::owner),
            Vec3i.CODEC.optionalFieldOf("size").forGetter(SchematicData::size)
    ).apply(instance, SchematicData::new));

    public static final StreamCodec<ByteBuf, SchematicData> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            SchematicData::uuid,
            UUIDUtil.STREAM_CODEC,
            SchematicData::owner,
            SIZE_STREAM_CODEC.apply(ByteBufCodecs::optional),
            SchematicData::size,
            SchematicData::new
    );
}
