package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ParticleDensityProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.PropMap;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.Property;
import com.hollingsworth.arsnouveau.api.registry.ParticleMotionRegistry;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public class SpiralMotion extends ParticleMotion {

    public static MapCodec<SpiralMotion> CODEC = buildPropCodec(SpiralMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, SpiralMotion> STREAM = buildStreamCodec(SpiralMotion::new);

    ParticleDensityProperty density;

    public SpiralMotion(PropMap propMap) {
        super(propMap);
        if(!propertyMap.has(ParticlePropertyRegistry.DENSITY_PROPERTY.get())){
            this.density = new ParticleDensityProperty(5, 0.3, SpawnType.SPHERE);
        } else {
            this.density = propertyMap.get(ParticlePropertyRegistry.DENSITY_PROPERTY.get());
        }
    }

    @Override
    public IParticleMotionType<?> getType() {
        return ParticleMotionRegistry.SPIRAL_TYPE.get();
    }
    @Override
    public void tick(ParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        double distance = Math.sqrt(Math.pow(x - prevX, 2) + Math.pow(y - prevY, 2) + Math.pow(z - prevZ, 2));
        float xRotRadians = (float) Math.toRadians(this.emitter.getRotation().x);
        float yRotRadians = (float) Math.toRadians(this.emitter.getRotation().y);
        int interpolationSteps = Math.max(1, (int) (distance / 0.1));
        double spiralRadius = density.radius();
        double spiralSpeed = 1.0f;
        for (int step = 0; step <= interpolationSteps; step++) {
            double t = (double) step / interpolationSteps;
            double interpolatedX = prevX + t * (x - prevX);
            double interpolatedY = prevY + t * (y - prevY);
            double interpolatedZ = prevZ + t * (z - prevZ);

            // Interpolate the angle for the current step
            double interpolatedAge = this.emitter.age + t;
            double angle = interpolatedAge * spiralSpeed;
            float localX = (float) (Math.cos(angle) * spiralRadius);
            float localZ = 0;
            float localY = (float) (Math.sin(angle) * spiralRadius);
            Matrix4f transform = new Matrix4f();
            transform.identity()
                    .translate(new Vector3f((float) interpolatedX, (float) interpolatedY, (float) interpolatedZ))
                    .rotateY(yRotRadians)
                    .rotateX(-xRotRadians);

            Vector3f localPos = new Vector3f(localX, localY, localZ);
            transform.transformPosition(localPos);
            level.addParticle(particleOptions, localPos.x, localPos.y, localPos.z, ParticleUtil.inRange(-0.05, 0.05),
                    ParticleUtil.inRange(0, 0.05),
                    ParticleUtil.inRange(-0.05, 0.05));
        }
    }

    @Override
    public List<Property<?>> getProperties() {
        return  List.of(new ParticleDensityProperty(propertyMap, 5, 20, 1, false, true));
    }
}
