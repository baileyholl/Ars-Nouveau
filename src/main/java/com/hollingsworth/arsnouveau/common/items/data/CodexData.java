package com.hollingsworth.arsnouveau.common.items.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public record CodexData(Optional<UUID> uuid, String playerName, List<ResourceLocation> glyphIds) implements TooltipProvider {
    public static Codec<CodexData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.optionalFieldOf("uuid").forGetter(CodexData::uuid),
            Codec.STRING.optionalFieldOf("playerName", "").forGetter(CodexData::playerName),
            Codec.list(ResourceLocation.CODEC).fieldOf("glyphIds").forGetter(CodexData::glyphIds)
    ).apply(instance, CodexData::new));

    public static StreamCodec<RegistryFriendlyByteBuf, CodexData> STREAM = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs::optional),
            CodexData::uuid,
            ByteBufCodecs.STRING_UTF8,
            CodexData::playerName,
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.collection(NonNullList::createWithCapacity)
    ), CodexData::glyphIds, CodexData::new);

    public CodexData(UUID uuid, String playerName, List<ResourceLocation> glyphIds) {
        this(Optional.of(uuid), playerName, glyphIds);
    }

    public boolean wasRecorded(){
        return uuid.isPresent() && playerName != null && !playerName.isEmpty();
    }

    @Override
    public void addToTooltip(Item.TooltipContext pContext, Consumer<Component> pTooltipAdder, TooltipFlag pTooltipFlag) {
        if (this.glyphIds().isEmpty()) {
            pTooltipAdder.accept(Component.translatable("ars_nouveau.codex_tooltip"));
        } else {
            pTooltipAdder.accept(Component.translatable("ars_nouveau.contains_glyphs", glyphIds().size()));
        }
        if (this.wasRecorded())
            pTooltipAdder.accept(Component.translatable("ars_nouveau.recorded_by", playerName()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodexData codexData = (CodexData) o;
        return Objects.equals(playerName, codexData.playerName) && Objects.equals(uuid, codexData.uuid) && Objects.equals(glyphIds, codexData.glyphIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, playerName, glyphIds);
    }
}
