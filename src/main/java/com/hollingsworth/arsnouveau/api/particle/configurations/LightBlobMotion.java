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

public class LightBlobMotion extends ParticleMotion {

    public static MapCodec<LightBlobMotion> CODEC = buildPropCodec(LightBlobMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, LightBlobMotion> STREAM =  buildStreamCodec(LightBlobMotion::new);

    public LightBlobMotion() {
        this(new PropMap());
    }

    public LightBlobMotion(PropMap propertyMap) {
        super(propertyMap);
    }

    @Override
    public IParticleMotionType<?> getType() {
        return ParticleMotionRegistry.LIGHT_BLOB.get();
    }

    @Override
    public void tick(PropertyParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        ParticleDensityProperty density = getDensity(particleOptions, 20, 0.1f);
        for(int i = 0; i < getNumParticles(density.density()); i++) {
            Vec3 speed = randomSpeed(particleOptions, 0.0, 0.0, 0.0 ,0.01);
            Vec3 adjustedVec = getMotionScaled(new Vec3(x, y, z), density.radius(), density.spawnType().orElse(SpawnType.SPHERE));
            Vector3f worldSpaceSpeed = toEmitterSpace( 0, 0,0, (float) speed.x, (float) speed.y, (float) speed.z);
            level.addAlwaysVisibleParticle(particleOptions, true, adjustedVec.x, adjustedVec.y, adjustedVec.z, worldSpaceSpeed.x, worldSpaceSpeed.y, worldSpaceSpeed.z);
        }
    }

    @Override
    public List<BaseProperty<?>> getProperties(PropMap propMap) {
        return List.of(propMap.createIfMissing(new ParticleTypeProperty()),
                propMap.createIfMissing(new ParticleDensityProperty(20, 0.1, SpawnType.SPHERE)
                        .minDensity(1)
                        .maxDensity(200)
                        .densityStepSize(1)),
                propMap.createIfMissing(new SpeedProperty(0, 0.01, 0, 0)));
    }
}
