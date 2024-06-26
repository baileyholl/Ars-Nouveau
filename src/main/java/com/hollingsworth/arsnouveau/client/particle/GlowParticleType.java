package com.hollingsworth.arsnouveau.client.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class GlowParticleType extends ParticleType<ColorParticleTypeData> {
    public GlowParticleType() {
        super(false);
    }

    @Override
    public MapCodec<ColorParticleTypeData> codec() {
        return ColorParticleTypeData.CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, ColorParticleTypeData> streamCodec() {
        return ColorParticleTypeData.STREAM_CODEC;
    }
}
