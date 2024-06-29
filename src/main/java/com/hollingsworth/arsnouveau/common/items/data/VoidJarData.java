package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.api.item.NBTComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

public record VoidJarData(ItemScrollData scrollData, boolean active) implements NBTComponent<VoidJarData>, TooltipProvider {

    public VoidJarData(){
        this(new ItemScrollData(), false);
    }

    public static final Codec<VoidJarData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ItemScrollData.CODEC.fieldOf("scrollData").forGetter(VoidJarData::scrollData),
        Codec.BOOL.fieldOf("active").forGetter(VoidJarData::active)
    ).apply(instance, VoidJarData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, VoidJarData> STREAM_CODEC = StreamCodec.composite(ItemScrollData.STREAM_CODEC, VoidJarData::scrollData, ByteBufCodecs.BOOL, VoidJarData::active, VoidJarData::new);


    public VoidJarData setActive(boolean active) {
        return new VoidJarData(scrollData, active);
    }

    @Override
    public Codec<VoidJarData> getCodec() {
        return CODEC;
    }

    @Override
    public void addToTooltip(Item.TooltipContext pContext, Consumer<Component> pTooltipAdder, TooltipFlag pTooltipFlag) {
        if (active) {
            pTooltipAdder.accept(Component.translatable("ars_nouveau.on"));
        } else {
            pTooltipAdder.accept(Component.translatable("ars_nouveau.off"));
        }
        scrollData.addToTooltip(pContext, pTooltipAdder, pTooltipFlag);
    }
}
