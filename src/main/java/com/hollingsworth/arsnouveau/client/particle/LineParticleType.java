package com.hollingsworth.arsnouveau.client.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class LineParticleType extends ParticleType<ColoredDynamicTypeData> {
    public LineParticleType() {
        super(false);
    }

    @Override
    public MapCodec<ColoredDynamicTypeData> codec() {
        return ColoredDynamicTypeData.CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, ColoredDynamicTypeData> streamCodec() {
        return ColoredDynamicTypeData.STREAM_CODEC;
    }
}
