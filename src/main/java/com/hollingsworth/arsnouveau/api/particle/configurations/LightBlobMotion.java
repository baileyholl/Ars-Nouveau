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
import net.minecraft.world.phys.Vec3;

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
        ParticleDensityProperty density = getDensity(particleOptions);
        for(int i = 0; i < getNumParticles(density.density()); i++) {
            Vec3 adjustedVec = getMotionScaled(new Vec3(x, y, z), density.radius(), density.spawnType().orElse(SpawnType.SPHERE));
            level.addAlwaysVisibleParticle(particleOptions, true, adjustedVec.x, adjustedVec.y, adjustedVec.z, 0, 0, 0);
        }
    }

    @Override
    public List<BaseProperty<?>> getProperties(PropMap propMap) {
        return List.of(new ParticleTypeProperty(propMap), new ParticleDensityProperty(propMap, 20, 0.1)
                .minDensity(1)
                .maxDensity(200)
                .densityStepSize(1));
    }
}
