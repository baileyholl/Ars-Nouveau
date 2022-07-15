package com.hollingsworth.arsnouveau.client.particle;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;

public class SparkleParticleType extends ParticleType<ColoredDynamicTypeData> {

    public SparkleParticleType() {
        super(false, ColoredDynamicTypeData.DESERIALIZER);
    }

    @Override
    public Codec<ColoredDynamicTypeData> codec() {
        return ColoredDynamicTypeData.CODEC;
    }

}
