package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.configurations.properties.PropMap;
import com.hollingsworth.arsnouveau.api.registry.ParticleMotionRegistry;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

public class WaveMotion extends ParticleMotion{

    public static MapCodec<WaveMotion> CODEC = buildPropCodec(WaveMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, WaveMotion> STREAM = buildStreamCodec(WaveMotion::new);

    public WaveMotion(PropMap propertyMap) {
        super(propertyMap);
    }

    @Override
    public void tick(ParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        int age = emitter.age;
        if(age == 0)
            return;
        double waveSpeed = 1;
        double amplitude = 0.5;
        double waveY = Math.sin(age * waveSpeed) * amplitude;
        int totalParticles = getNumParticles(20);

        double deltaX = x - prevX;
        double deltaY = y - prevY;
        double deltaZ = z - prevZ;

        float randomScale = 0.01f;
        for (int i = 0; i < totalParticles; i++) {
            double t = (double) i / Math.max(1, totalParticles - 1);
            double px = prevX + deltaX * t;
            double py = prevY + deltaY * t + waveY;
            double pz = prevZ + deltaZ * t;
            level.addParticle(particleOptions, px, py, pz,
                    ParticleUtil.inRange(-randomScale, randomScale),
                    ParticleUtil.inRange(-randomScale, randomScale),
                    ParticleUtil.inRange(-randomScale, randomScale));
        }
    }

    @Override
    public IParticleMotionType<?> getType() {
        return ParticleMotionRegistry.WAVE_TYPE.get();
    }
}
