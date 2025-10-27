package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.*;
import com.hollingsworth.arsnouveau.api.registry.ParticleMotionRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class SpiralMotion extends ParticleMotion {

    public static MapCodec<SpiralMotion> CODEC = buildPropCodec(SpiralMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, SpiralMotion> STREAM = buildStreamCodec(SpiralMotion::new);

    public SpiralMotion(PropMap propMap) {
        super(propMap);
    }

    @Override
    public IParticleMotionType<?> getType() {
        return ParticleMotionRegistry.SPIRAL_TYPE.get();
    }

    @Override
    public void tick(PropertyParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        ParticleDensityProperty density = getDensity(particleOptions, 100, 0.3f);
        double spiralRadius = density.radius();
        int totalParticles = getNumParticles(density.density());
        for (int step = 0; step <= totalParticles; step++) {
            double t = (double) step / totalParticles;
            double interpolatedX = prevX + t * (x - prevX);
            double interpolatedY = prevY + t * (y - prevY);
            double interpolatedZ = prevZ + t * (z - prevZ);

            // Interpolate the angle for the current step
            double angle = this.emitter.age + t;
            float localX = (float) (Math.cos(angle) * spiralRadius);
            float localZ = 0;
            float localY = (float) (Math.sin(angle) * spiralRadius);
            Vector3f localPos = toEmitterSpace((float) interpolatedX, (float) interpolatedY, (float) interpolatedZ, localX, localY, localZ);
            Vec3 speed = randomSpeed(particleOptions);
            level.addAlwaysVisibleParticle(particleOptions, true, localPos.x, localPos.y, localPos.z, speed.x, speed.y, speed.z);
        }
    }

    @Override
    public List<BaseProperty<?>> getProperties(PropMap propMap) {
        return List.of(
                propMap.createIfMissing(new ParticleTypeProperty()),
                propMap.createIfMissing(new ParticleDensityProperty(100, 0.3f, SpawnType.SPHERE)
                        .maxDensity(200)
                        .minDensity(20)
                        .densityStepSize(5)
                        .supportsShapes(false)
                        .supportsRadius(true)),
                propMap.createIfMissing(new SpeedProperty().yRange(-0.05, 0.05).xzRange(-0.05, 0.05)));
    }
}
