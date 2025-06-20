package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface IPropertyType<T extends BaseProperty> {
    StreamCodec<RegistryFriendlyByteBuf, IPropertyType<?>> STREAM_CODEC = StreamCodec.recursive(
            p_330812_ -> ByteBufCodecs.registry(ParticlePropertyRegistry.PARTICLE_PROPERTY_KEY)
    );
    MapCodec<T> codec();

    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

    default Codec<T> normalCodec() {
        return codec().codec();
    }

    default String getString(){
        return ParticlePropertyRegistry.PARTICLE_PROPERTY_REGISTRY.getKey(this).toString();
    }
}
