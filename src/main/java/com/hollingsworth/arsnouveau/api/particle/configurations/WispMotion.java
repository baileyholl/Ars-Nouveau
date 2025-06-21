package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.*;
import com.hollingsworth.arsnouveau.api.registry.ParticleMotionRegistry;
import com.hollingsworth.arsnouveau.api.util.NoiseGenerator;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class WispMotion extends ParticleMotion {

    public static MapCodec<WispMotion> CODEC = buildPropCodec(WispMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, WispMotion> STREAM = buildStreamCodec(WispMotion::new);

    public WispMotion() {
        this(new PropMap());
    }

    public WispMotion(PropMap propertyMap) {
        super(propertyMap);
    }

    @Override
    public IParticleMotionType<?> getType() {
        return ParticleMotionRegistry.WISP_TYPE.get();
    }

    NoiseGenerator noise;

    @Override
    public void tick(PropertyParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        if (noise == null) {
            noise = new NoiseGenerator();
        }
        ParticleDensityProperty density = getDensity(particleOptions, 20, 0.1f);
        int numParticles = getNumParticles(density.density());
        double flicker = 0.65;
        double randomScale = density.radius() * 0.03;
        double yDrift = 1.0; // Can be between 0, 1
        double xzDrift = 0.1f;
        for (int i = 0; i < numParticles; i++) {
            double t = (double) i / numParticles;
            double interpX = prevX + x * t;
            double interpY = prevY + y * t;
            double interpZ = prevZ + z * t;

            double flameBaseT = (emitter.age + t) * xzDrift;
            double flickerX = noise.noise(flameBaseT, 100, 0) * flicker;
            double flickerZ = noise.noise(flameBaseT + 50, 200, 0) * flicker;
            double localY = noise.noise(flameBaseT + 100, 300, 0) * yDrift;

            Vec3 local = new Vec3(flickerX, localY * 0.5, flickerZ);

            Vec3 speed = randomSpeed(particleOptions);

            Vector3f pos = toEmitterSpace(
                    (float) interpX, (float) interpY, (float) interpZ,
                    (float) local.x, (float) local.y, (float) local.z
            );

            level.addParticle(particleOptions, pos.x, pos.y, pos.z,
                    speed.x,
                    speed.y,
                    speed.z);
        }
    }

    @Override
    public List<BaseProperty<?>> getProperties(PropMap propMap) {
        return List.of(propMap.createIfMissing(new ParticleTypeProperty()), propMap.createIfMissing(new ParticleDensityProperty(20, 0.1, SpawnType.SPHERE)
                        .minDensity(1)
                        .maxDensity(200)
                        .densityStepSize(1)),
                propMap.createIfMissing(new SpeedProperty().yRange(0.01, 0.02)));
    }
}
