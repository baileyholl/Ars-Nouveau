package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.*;
import com.hollingsworth.arsnouveau.api.registry.ParticleMotionRegistry;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BrazierMotion extends ParticleMotion {

    public static MapCodec<BrazierMotion> CODEC = buildPropCodec(BrazierMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, BrazierMotion> STREAM =  buildStreamCodec(BrazierMotion::new);


    public BrazierMotion(PropMap propertyMap) {
        super(propertyMap);
    }

    @Override
    public IParticleMotionType<?> getType() {
        return ParticleMotionRegistry.BRAZIER_TYPE.get();
    }

    @Override
    public void tick(PropertyParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        ParticleDensityProperty density = getDensity(particleOptions, 100, 0.25f);
        double xzOffset = density.radius();
        int numParticles = getNumParticles(density.density());

        PropertyParticleOptions particle2;
        var secondType = propertyMap.get(ParticlePropertyRegistry.TYPE_PROPERTY.get());
        if(secondType != null){
            particle2 = new PropertyParticleOptions(propertyMap);
        }else{
            particle2 = new PropertyParticleOptions(PropertyParticleOptions.defaultPropMap());
        }

        for (int i = 0; i < numParticles ; i++) {
            Vec3 speed = randomSpeed(particleOptions, 0.0f, 0.0f, 0.0f, 0.05f);
            level.addAlwaysVisibleParticle(
                    particleOptions,
                    false,
                    x + ParticleUtil.inRange(-xzOffset / 2, xzOffset / 2), y + ParticleUtil.inRange(-0.05, 0.2), z + ParticleUtil.inRange(-xzOffset / 2, xzOffset / 2),
                    speed.x, speed.y, speed.z);

            Vec3 speed2 = randomSpeed(particleOptions, 0.0f, 0.0f, 0.0f, 0.05f);
            level.addAlwaysVisibleParticle(
                    particle2,
                    false,
                    x + ParticleUtil.inRange(-xzOffset, xzOffset), y + ParticleUtil.inRange(0, 0.7), z + ParticleUtil.inRange(-xzOffset, xzOffset),
                    speed2.x, speed2.y, speed2.z);
        }
    }

    @Override
    public List<BaseProperty<?>> getProperties(PropMap propMap) {
        return List.of(propMap.createIfMissing(new ParticleTypeProperty()),
                propertyMap.createIfMissing(new ParticleTypeProperty()),
                propMap.createIfMissing(new ParticleDensityProperty(100, 0.25, SpawnType.SPHERE)
                        .supportsShapes(false)
                        .minDensity(1)
                        .maxDensity(200)
                        .densityStepSize(1)),
                propMap.createIfMissing(new SpeedProperty(0.0f, 0.05f, 0.0f, 0.0f)));
    }
}
