package com.hollingsworth.arsnouveau.client.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class SparkleParticleType extends ParticleType<ColoredDynamicTypeData> {

    public SparkleParticleType() {
        super(false);
    }

    @Override
    public MapCodec<ColoredDynamicTypeData> codec() {
        return ColoredDynamicTypeData.CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, ColoredDynamicTypeData> streamCodec() {
        return null;
    }

}
