package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record BlockFillContents(int amount) {
    public static final Codec<BlockFillContents> CODEC = Codec.INT.xmap(BlockFillContents::new, BlockFillContents::amount);
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockFillContents> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, BlockFillContents::amount, BlockFillContents::new);

    public static int get(ItemStack stack){
        return stack.getOrDefault(DataComponentRegistry.BLOCK_FILL_CONTENTS, new BlockFillContents(0)).amount;
    }
}
