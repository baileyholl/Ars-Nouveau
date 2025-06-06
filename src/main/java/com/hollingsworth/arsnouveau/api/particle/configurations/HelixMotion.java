package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.BaseProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ParticleDensityProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ParticleTypeProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.PropMap;
import com.hollingsworth.arsnouveau.api.registry.ParticleMotionRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

import java.util.List;

public class HelixMotion extends ParticleMotion {

    public static MapCodec<HelixMotion> CODEC = buildPropCodec(HelixMotion::new);


    public static StreamCodec<RegistryFriendlyByteBuf, HelixMotion> STREAM = buildStreamCodec(HelixMotion::new);


    public HelixMotion(PropMap propMap) {
        super(propMap);
    }

    @Override
    public IParticleMotionType<?> getType() {
        return ParticleMotionRegistry.HELIX_TYPE.get();
    }

    @Override
    public void tick(PropertyParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        ParticleDensityProperty densityProperty = getDensity(particleOptions);
        double radius = densityProperty.radius();
        int totalParticles = getNumParticles(densityProperty.density());
        for (int step = 0; step <= totalParticles; step++) {
            double t = (double) step / totalParticles;
            double interpolatedX = prevX + t * (x - prevX);
            double interpolatedY = prevY + t * (y - prevY);
            double interpolatedZ = prevZ + t * (z - prevZ);

            // Interpolate the angle for the current step
            double interpolatedAge = this.emitter.age + t;
            double angle = interpolatedAge;
            float localX = (float) ((float) (Math.sin(angle) * radius));
            float localY = (float) ((float) (Math.cos(angle) * radius));
            float localZ = 0;

            Vector3f spiralOne = toEmitterSpace((float) interpolatedX, (float) interpolatedY, (float) interpolatedZ, localX, localY, localZ);
            Vector3f opposite = toEmitterSpace((float) interpolatedX, (float) interpolatedY, (float) interpolatedZ, -localX, -localY, -localZ);
            level.addParticle(particleOptions, spiralOne.x, spiralOne.y, spiralOne.z, 0, 0, 0);
            level.addParticle(particleOptions, opposite.x, opposite.y, opposite.z, 0, 0, 0);
        }
    }

    @Override
    public List<BaseProperty<?>> getProperties(PropMap propMap) {
        return List.of(new ParticleTypeProperty(propMap), new ParticleDensityProperty(propMap, 100, 0.3f));
    }
}
