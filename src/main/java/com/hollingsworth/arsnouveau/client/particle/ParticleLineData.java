package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.client.registry.ModParticles;
import net.minecraft.core.particles.ParticleOptions;

import java.util.Random;

@Deprecated
public class ParticleLineData {

    public static Random random = new Random();

    public static ParticleOptions createData(ParticleColor color) {
        return new ColoredDynamicTypeData(ModParticles.LINE_TYPE.get(), color, (float) ParticleUtil.inRange(0.05, 0.15), 40 + random.nextInt(20));
    }

    public static ParticleOptions createData(ParticleColor color, float scale, int age) {
        return new ColoredDynamicTypeData(ModParticles.LINE_TYPE.get(), color, scale, age);
    }
}