package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.configurations.properties.PropMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;
import java.util.function.Supplier;

public record SimpleParticleMotionType<T extends ParticleMotion>(MapCodec<T> codec,
                                                                 StreamCodec<RegistryFriendlyByteBuf, T> streamCodec,
                                                                 Supplier<T> createConfigured,
                                                                 Function<PropMap, T> copy) implements IParticleMotionType<T> {


    public SimpleParticleMotionType(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, Function<PropMap, T> copy) {
        this(codec, streamCodec, () -> copy.apply(new PropMap()), copy);
    }

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

    @Override
    public T create(PropMap propMap) {
        return copy.apply(propMap);
    }
}
