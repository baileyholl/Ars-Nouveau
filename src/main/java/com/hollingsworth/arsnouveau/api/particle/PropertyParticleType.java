package com.hollingsworth.arsnouveau.api.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class PropertyParticleType extends ParticleType<PropertyParticleOptions> {

    public PropertyParticleType() {
        super(false);
    }

    @Override
    public MapCodec<PropertyParticleOptions> codec() {
        return PropertyParticleOptions.CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, PropertyParticleOptions> streamCodec() {
        return PropertyParticleOptions.STREAM_CODEC;
    }
}
