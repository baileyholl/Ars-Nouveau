package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record SimplePropertyType<T extends BaseProperty>(MapCodec<T> codec,
                                                         StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) implements IPropertyType<T> {
    @Override
    public MapCodec<T> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        return streamCodec;
    }
}
