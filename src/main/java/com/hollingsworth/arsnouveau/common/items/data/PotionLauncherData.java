package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.api.registry.PotionProviderRegistry;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.Objects;

public record PotionLauncherData(PotionContents renderData, int amountLeft, int lastSlot) {

    public static MapCodec<PotionLauncherData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        PotionContents.CODEC.fieldOf("lastDataForRender").forGetter(PotionLauncherData::renderData),
        Codec.INT.fieldOf("amountLeft").forGetter(PotionLauncherData::amountLeft),
        Codec.INT.fieldOf("lastSlot").forGetter(PotionLauncherData::lastSlot)
    ).apply(instance, PotionLauncherData::new));

    public static StreamCodec<RegistryFriendlyByteBuf, PotionLauncherData> STREAM = StreamCodec.composite(
            PotionContents.STREAM_CODEC,
            PotionLauncherData::renderData,
            ByteBufCodecs.INT,
            PotionLauncherData::amountLeft,
            ByteBufCodecs.INT,
            PotionLauncherData::lastSlot,
            PotionLauncherData::new
    );

    public PotionLauncherData(){
        this(PotionContents.EMPTY, 0, -1);
    }

    public PotionContents getPotionDataFromSlot(Player player){
        if(lastSlot < 0 || lastSlot >= player.inventory.getContainerSize())
            return PotionContents.EMPTY;
        ItemStack stack = player.inventory.getItem(lastSlot);
        var provider = PotionProviderRegistry.from(stack);
        return provider == null ? PotionContents.EMPTY : provider.getPotionData(stack);
    }

    public PotionContents expendPotion(Player player, ItemStack launcherStack){
        if(lastSlot >= player.inventory.getContainerSize())
            return PotionContents.EMPTY;
        ItemStack item = player.inventory.getItem(lastSlot);
        var provider = PotionProviderRegistry.from(item);
        if(provider == null){
            return PotionContents.EMPTY;
        }
        if(provider.usesRemaining(item) <= 0){
            return PotionContents.EMPTY;
        }
        PotionContents contents = provider.getPotionData(item);
        provider.consumeUses(item, 1, player);
        launcherStack.set(DataComponentRegistry.POTION_LAUNCHER, new PotionLauncherData(provider.getPotionData(item), provider.usesRemaining(item), lastSlot));
        return contents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PotionLauncherData that = (PotionLauncherData) o;
        return lastSlot == that.lastSlot && amountLeft == that.amountLeft && Objects.equals(renderData, that.renderData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(renderData, amountLeft, lastSlot);
    }
}
