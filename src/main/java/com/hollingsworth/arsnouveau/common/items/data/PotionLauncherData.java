package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.api.potion.IPotionProvider;
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

import javax.annotation.Nullable;
import java.util.Objects;

public record PotionLauncherData(PotionContents renderData, int lastSlot) {

    public static MapCodec<PotionLauncherData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PotionContents.CODEC.fieldOf("lastDataForRender").forGetter(PotionLauncherData::renderData),
            Codec.INT.fieldOf("lastSlot").forGetter(PotionLauncherData::lastSlot)
    ).apply(instance, PotionLauncherData::new));

    public static StreamCodec<RegistryFriendlyByteBuf, PotionLauncherData> STREAM = StreamCodec.composite(
            PotionContents.STREAM_CODEC,
            PotionLauncherData::renderData,
            ByteBufCodecs.INT,
            PotionLauncherData::lastSlot,
            PotionLauncherData::new
    );

    public PotionLauncherData() {
        this(PotionContents.EMPTY, -1);
    }

    public @Nullable IPotionProvider getPotionDataFromSlot(Player player) {
        ItemStack stack = getSelectedStack(player);
        return PotionProviderRegistry.from(stack);
    }

    public ItemStack getSelectedStack(Player player) {
        if (lastSlot < 0 || lastSlot >= player.inventory.getContainerSize())
            return ItemStack.EMPTY;
        return player.inventory.getItem(lastSlot);
    }

    public PotionContents expendPotion(Player player, ItemStack launcherStack) {
        if (lastSlot >= player.inventory.getContainerSize())
            return PotionContents.EMPTY;
        ItemStack item = player.inventory.getItem(lastSlot);
        var provider = PotionProviderRegistry.from(item);
        if (provider == null) {
            return PotionContents.EMPTY;
        }
        if (provider.usesRemaining(item) <= 0) {
            return PotionContents.EMPTY;
        }
        PotionContents contents = provider.getPotionData(item);
        provider.consumeUses(item, 1, player);
        launcherStack.set(DataComponentRegistry.POTION_LAUNCHER, new PotionLauncherData(provider.getPotionData(item), lastSlot));
        return contents;
    }

    public int amountLeft(Player player) {
        ItemStack stack = getSelectedStack(player);
        var provider = PotionProviderRegistry.from(stack);
        if (provider == null) {
            return 0;
        }
        return provider.usesRemaining(stack);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PotionLauncherData that = (PotionLauncherData) o;
        return lastSlot == that.lastSlot && Objects.equals(renderData, that.renderData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(renderData, lastSlot);
    }
}
