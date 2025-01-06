package com.hollingsworth.arsnouveau.client.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class HelixParticleType extends ParticleType<HelixParticleTypeData> {
    public HelixParticleType() {
        super(false);
    }

    @Override
    public MapCodec<HelixParticleTypeData> codec() {
        return HelixParticleTypeData.CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, HelixParticleTypeData> streamCodec() {
        return HelixParticleTypeData.STREAM_CODEC;
    }

}
