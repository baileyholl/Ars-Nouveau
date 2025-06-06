package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.BaseProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ParticleDensityProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ParticleTypeProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.PropMap;
import com.hollingsworth.arsnouveau.api.registry.ParticleMotionRegistry;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

import java.util.List;

public class WaveMotion extends ParticleMotion{

    public static MapCodec<WaveMotion> CODEC = buildPropCodec(WaveMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, WaveMotion> STREAM = buildStreamCodec(WaveMotion::new);

    public WaveMotion(PropMap propMap) {
        super(propMap);
    }

    @Override
    public void tick(PropertyParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        ParticleDensityProperty density = getDensity(particleOptions);
        int age = emitter.age;
        if(age == 0)
            return;

        double amplitude = density.radius();
        int totalParticles = getNumParticles(density.density());

        double deltaX = x - prevX;
        double deltaY = y - prevY;
        double deltaZ = z - prevZ;

        float randomScale = 0.01f;
        for (int i = 0; i < totalParticles; i++) {

            double t = (double) i / Math.max(1, totalParticles - 1);
            double waveY = Math.sin(age + t) * amplitude;
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

    @Override
    public List<BaseProperty<?>> getProperties(PropMap propMap) {
        return  List.of(new ParticleTypeProperty(propMap), new ParticleDensityProperty(propMap, 100, 0.3f)
                .maxDensity(200)
                .minDensity(20)
                .minRadius(0.1f)
                .densityStepSize(5)
                .supportsShapes(false)
                .supportsRadius(true));
    }
}
