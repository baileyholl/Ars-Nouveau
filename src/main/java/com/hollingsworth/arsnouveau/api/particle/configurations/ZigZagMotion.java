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
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class ZigZagMotion extends ParticleMotion {

    public static MapCodec<ZigZagMotion> CODEC = buildPropCodec(ZigZagMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, ZigZagMotion> STREAM = buildStreamCodec(ZigZagMotion::new);

    public ZigZagMotion(PropMap propMap) {
        super(propMap);
    }

    @Override
    public void tick(PropertyParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        int age = emitter.age;
        if (age == 0)
            return;
        ParticleDensityProperty density = getDensity(particleOptions, 100, 0.3f);
        int totalParticles = getNumParticles(density.density());
        float randomScale = 0.00f;
        double radius = density.radius();
        for (int step = 0; step <= totalParticles; step++) {
            double t = (double) step / totalParticles;
            double interpolatedX = prevX + t * (x - prevX);
            double interpolatedY = prevY + t * (y - prevY);
            double interpolatedZ = prevZ + t * (z - prevZ);
            double motionProgress = age + t;
            Vec3 local = getZigzag3D(motionProgress, 8, radius, 12, radius - radius * 0.3, 0.1);
            Vector3f transformed = toEmitterSpace((float) interpolatedX, (float) interpolatedY, (float) interpolatedZ,
                    (float) local.x(), (float) local.y(), (float) local.z);

            level.addParticle(particleOptions, transformed.x, transformed.y, transformed.z,
                    ParticleUtil.inRange(-randomScale, randomScale),
                    ParticleUtil.inRange(-randomScale, randomScale),
                    ParticleUtil.inRange(-randomScale, randomScale));
        }
    }

    public Vec3 getZigzag3D(double age, double freqXZ, double ampXZ, double freqY, double ampY, double forwardSpeed) {
        double t = age * forwardSpeed;
        double offsetX = Math.sin(t * freqXZ) * ampXZ;
        double offsetY = Math.sin(t * freqY) * ampY;

        return new Vec3(offsetX, offsetY, 0);
    }

    @Override
    public IParticleMotionType<?> getType() {
        return ParticleMotionRegistry.ZIGZAG_TYPE.get();
    }

    @Override
    public List<BaseProperty<?>> getProperties(PropMap propMap) {
        return List.of(propMap.createIfMissing(new ParticleTypeProperty()), propMap.createIfMissing(new ParticleDensityProperty(100, 0.3f, SpawnType.SPHERE)
                .maxDensity(200)
                .minDensity(20)
                .densityStepSize(5)
                .supportsShapes(false)
                .supportsRadius(true)));
    }
}
