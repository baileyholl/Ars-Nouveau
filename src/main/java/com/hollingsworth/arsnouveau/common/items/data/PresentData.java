package com.hollingsworth.arsnouveau.common.items.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public record PresentData(String name, UUID uuid) {
    public static Codec<PresentData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(PresentData::name),
            Codec.STRING.xmap(UUID::fromString, UUID::toString).fieldOf("uuid").forGetter(PresentData::uuid)
    ).apply(instance, PresentData::new));

    public static StreamCodec<RegistryFriendlyByteBuf, PresentData> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, PresentData::name, ByteBufCodecs.STRING_UTF8, p -> p.uuid().toString(), PresentData::new);

    public PresentData(String name, String uuid){
        this(name, UUID.fromString(uuid));
    }
}
