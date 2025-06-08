package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.*;
import com.hollingsworth.arsnouveau.api.registry.ParticleMotionRegistry;
import com.hollingsworth.arsnouveau.api.util.NoiseGenerator;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class WispMotion extends ParticleMotion {

    public static MapCodec<WispMotion> CODEC = buildPropCodec(WispMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, WispMotion> STREAM =  buildStreamCodec(WispMotion::new);

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
        if(noise == null){
            noise = new NoiseGenerator();
        }
        ParticleDensityProperty density = getDensity(particleOptions, 20, 0.1f);
        int numParticles = getNumParticles(density.density());
        double noiseScale = 0.1;
        double flicker = 0.65;
        float randomScale = 0.01f;
        for (int i = 0; i < numParticles; i++) {
            double t = (double) i / numParticles;
            double interpX = prevX + x * t;
            double interpY = prevY + y * t;
            double interpZ = prevZ + z * t;

            double flameBaseT = (emitter.age + t) * noiseScale;
            double flickerX = noise.noise(flameBaseT, 100, 0) * flicker;
            double flickerZ = noise.noise(flameBaseT + 50, 200, 0) * flicker;
            double localY = noise.noise(flameBaseT + 100, 300, 0) * 1; // in range ~[0,1]

            Vec3 local = new Vec3(flickerX, localY * 0.5, flickerZ);

            Vector3f pos = toEmitterSpace(
                    (float) interpX, (float) interpY, (float) interpZ,
                    (float) local.x, (float) local.y, (float) local.z
            );

            level.addParticle(particleOptions, pos.x, pos.y, pos.z,
                    ParticleUtil.inRange(-randomScale, randomScale),
                    ParticleUtil.inRange(0.01f, 0.02f),
                    ParticleUtil.inRange(-randomScale, randomScale));
        }
    }

    @Override
    public List<BaseProperty<?>> getProperties(PropMap propMap) {
        ParticleTypeProperty secondParticle = new ParticleTypeProperty(propertyMap);
        secondParticle.writeChanges();
        return List.of(new ParticleTypeProperty(propMap), secondParticle, new ParticleDensityProperty(propMap, 20, 0.1)
                .minDensity(1)
                .maxDensity(200)
                .densityStepSize(1), new SpeedProperty(propMap));
    }
}
