package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.*;
import com.hollingsworth.arsnouveau.api.registry.ParticleMotionRegistry;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class HelixMotion extends ParticleMotion {

    public static MapCodec<HelixMotion> CODEC = buildPropCodec(HelixMotion::new);


    public static StreamCodec<RegistryFriendlyByteBuf, HelixMotion> STREAM = buildStreamCodec(HelixMotion::new);


    public HelixMotion(PropMap propMap) {
        super(propMap);
        propMap.createIfMissing(new ParticleTypeProperty());
    }

    @Override
    public IParticleMotionType<?> getType() {
        return ParticleMotionRegistry.HELIX_TYPE.get();
    }

    @Override
    public void tick(PropertyParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        ParticleDensityProperty densityProperty = getDensity(particleOptions, 100, 0.3f);
        double radius = densityProperty.radius();
        int totalParticles = getNumParticles(densityProperty.density());
        PropertyParticleOptions particle2;
        var secondType = propertyMap.get(ParticlePropertyRegistry.TYPE_PROPERTY.get());
        if(secondType != null){
            particle2 = new PropertyParticleOptions(propertyMap);
        }else{
            particle2 = new PropertyParticleOptions(PropertyParticleOptions.defaultPropMap());
        }
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
            Vec3 speedOne = randomSpeed(particleOptions);
            Vec3 speedTwo = randomSpeed(particle2);
            Vector3f realSpeed = toEmitterSpace((float) 0, (float) 0, (float) 0, (float) speedOne.x, (float) speedOne.y, 0);
            Vector3f realSpeed2 = toEmitterSpace((float) 0, (float) 0, (float) 0, (float) speedTwo.x, (float) speedTwo.y, 0);
            level.addAlwaysVisibleParticle(particleOptions, true, spiralOne.x, spiralOne.y, spiralOne.z,
                    realSpeed.x, realSpeed.y, realSpeed.z);
            level.addAlwaysVisibleParticle(particle2, true, opposite.x, opposite.y, opposite.z,
                    realSpeed2.x, realSpeed2.y, realSpeed2.z);
        }
    }

    @Override
    public List<BaseProperty<?>> getProperties(PropMap propMap) {
        return List.of(propMap.createIfMissing(new ParticleTypeProperty()),
                propertyMap.createIfMissing(new ParticleTypeProperty()),
                propMap.createIfMissing(new ParticleDensityProperty(100, 0.3f, SpawnType.SPHERE)),
                propMap.createIfMissing(new SpeedProperty()));
    }
}
