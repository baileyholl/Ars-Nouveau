package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public record PresentData(String name, Optional<UUID> uuid) implements TooltipProvider {
    public static Codec<PresentData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(PresentData::name),
            UUIDUtil.CODEC.optionalFieldOf("uuid").forGetter(PresentData::uuid)
    ).apply(instance, PresentData::new));

    public static StreamCodec<RegistryFriendlyByteBuf, PresentData> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, PresentData::name, ByteBufCodecs.STRING_UTF8, p -> p.uuid().toString(), PresentData::new);

    public PresentData(String name, String uuid) {
        this(name, Optional.ofNullable(uuid == null ? null : UUID.fromString(uuid)));
    }

    public PresentData() {
        this(null, (String) null);
    }

    @Override
    public void addToTooltip(Item.TooltipContext pContext, Consumer<Component> pTooltipAdder, TooltipFlag pTooltipFlag) {
        if (uuid().isPresent()) {
            if (uuid().get().equals(ArsNouveau.proxy.getPlayer().getUUID())) {
                pTooltipAdder.accept(Component.translatable("ars_nouveau.present.give"));
            } else {
                pTooltipAdder.accept(Component.translatable("ars_nouveau.present.from", name()).withStyle(ChatFormatting.GOLD));
            }
        }
    }
}
