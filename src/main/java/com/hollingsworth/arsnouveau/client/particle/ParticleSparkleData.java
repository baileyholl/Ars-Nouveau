package com.hollingsworth.arsnouveau.client.particle;

import net.minecraft.core.particles.ParticleOptions;

public class ParticleSparkleData {

    public static ParticleOptions createData(ParticleColor color) {
        return new ColoredDynamicTypeData(ModParticles.SPARKLE_TYPE.get(), color, .25f, 36);
    }

    public static ParticleOptions createData(ParticleColor color, float scale, int age) {
        return new ColoredDynamicTypeData(ModParticles.SPARKLE_TYPE.get(), color, scale, age);
    }

}