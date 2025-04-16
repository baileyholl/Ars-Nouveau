package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;

public record SimpleConfigurableParticleType<T extends IConfigurableParticle>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, Supplier<T> createConfigured) implements IConfigurableParticleType<T> {
    @Override
    public MapCodec<T> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        return streamCodec;
    }

    @Override
    public T create() {
        return createConfigured.get();
    }
}
