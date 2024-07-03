package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.common.crafting.recipes.CheatSerializer;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MobJarData(CompoundTag entityTag) {

    public static final Codec<MobJarData> CODEC = CompoundTag.CODEC.xmap(MobJarData::new, MobJarData::entityTag);
    public static final StreamCodec<RegistryFriendlyByteBuf, MobJarData> STREAM = CheatSerializer.create(CODEC);
}
