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
        ParticleDensityProperty density = getDensity(particleOptions);
        double spiralRadius = density.radius();
        int totalParticles = getNumParticles(density.density());
        System.out.println(density.density());
        double spiralSpeed = 1.0f;
        for (int step = 0; step <= totalParticles; step++) {
            double t = (double) step / totalParticles;
            double interpolatedX = prevX + t * (x - prevX);
            double interpolatedY = prevY + t * (y - prevY);
            double interpolatedZ = prevZ + t * (z - prevZ);

            // Interpolate the angle for the current step
            double interpolatedAge = this.emitter.age + t;
            double angle = interpolatedAge * spiralSpeed;
            float localX = (float) (Math.cos(angle) * spiralRadius);
            float localZ = 0;
            float localY = (float) (Math.sin(angle) * spiralRadius);
            Vector3f localPos = toEmitterSpace((float) interpolatedX, (float) interpolatedY, (float) interpolatedZ, localX, localY, localZ);
            level.addParticle(particleOptions, localPos.x, localPos.y, localPos.z, ParticleUtil.inRange(-0.05, 0.05),
                    ParticleUtil.inRange(-0.05, 0.05),
                    ParticleUtil.inRange(-0.05, 0.05));
        }
    }

    @Override
    public List<BaseProperty<?>> getProperties(PropMap propMap) {
        return List.of(new ParticleTypeProperty(propMap), new ParticleDensityProperty(propMap, 100, 0.3f)
                .maxDensity(200)
                .minDensity(20)
                .densityStepSize(5)
                .supportsShapes(false)
                .supportsRadius(true));
    }
}
