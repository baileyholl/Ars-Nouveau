package com.hollingsworth.arsnouveau.common.items.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.UUID;

public record CodexData(UUID uuid, String playerName, List<ResourceLocation> glyphIds) {
    public static Codec<CodexData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("uuid", "").forGetter(i -> i.uuid.toString()),
            Codec.STRING.fieldOf("playerName").forGetter(CodexData::playerName),
            Codec.list(ResourceLocation.CODEC).fieldOf("glyphIds").forGetter(CodexData::glyphIds)
    ).apply(instance, CodexData::new));

    public static StreamCodec<RegistryFriendlyByteBuf, CodexData> STREAM = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, i-> i.uuid().toString(), ByteBufCodecs.STRING_UTF8, CodexData::playerName, ResourceLocation.STREAM_CODEC.apply(
            ByteBufCodecs.collection(NonNullList::createWithCapacity)
    ), CodexData::glyphIds, CodexData::new);

    public CodexData(String uuid, String playerName, List<ResourceLocation> glyphIds) {
        this(UUID.fromString(uuid), playerName, glyphIds);
    }
}
