package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.api.potion.IPotionProvider;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record MultiPotionContents(int charges, PotionContents contents, int maxUses) implements IPotionProvider {
    public static Codec<MultiPotionContents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("charges").forGetter(MultiPotionContents::charges),
            PotionContents.CODEC.fieldOf("contents").forGetter(MultiPotionContents::contents),
            Codec.INT.fieldOf("maxUses").forGetter(MultiPotionContents::maxUses)
    ).apply(instance, MultiPotionContents::new));

    public static StreamCodec<RegistryFriendlyByteBuf, MultiPotionContents> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, MultiPotionContents::charges, PotionContents.STREAM_CODEC, MultiPotionContents::contents,ByteBufCodecs.INT, MultiPotionContents::maxUses, MultiPotionContents::new);

    public MultiPotionContents withCharges(int charges){
        return new MultiPotionContents(charges, contents, maxUses);
    }

    public MultiPotionContents withMaxUses(int maxUses){
        return new MultiPotionContents(charges, contents, maxUses);
    }

    public MultiPotionContents withContents(PotionContents contents){
        return new MultiPotionContents(charges, contents, maxUses);
    }

    @Override
    public @NotNull PotionContents getPotionData(ItemStack stack) {
        return contents;
    }

    @Override
    public int usesRemaining(ItemStack stack) {
        return charges;
    }

    @Override
    public int maxUses(ItemStack stack) {
        return maxUses;
    }

    @Override
    public void consumeUses(ItemStack stack, int amount, Player player) {
        stack.set(DataComponentRegistry.MULTI_POTION, withCharges(charges - amount));
    }

    @Override
    public void addUse(ItemStack stack, int amount, @Nullable Player player) {
        stack.set(DataComponentRegistry.MULTI_POTION, withCharges(charges + amount));
    }
}
