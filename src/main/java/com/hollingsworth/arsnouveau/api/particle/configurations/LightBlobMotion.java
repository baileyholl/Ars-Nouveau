package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ParticleDensityProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.PropMap;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.Property;
import com.hollingsworth.arsnouveau.api.registry.ParticleMotionRegistry;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class LightBlobMotion extends ParticleMotion {

    public static MapCodec<LightBlobMotion> CODEC = buildPropCodec(LightBlobMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, LightBlobMotion> STREAM =  buildStreamCodec(LightBlobMotion::new);

    ParticleDensityProperty density;

    public LightBlobMotion() {
        this(new PropMap());
    }

    public LightBlobMotion(PropMap propertyMap) {
        super(propertyMap);
        if(!propertyMap.has(ParticlePropertyRegistry.DENSITY_PROPERTY.get())){
            this.density = new ParticleDensityProperty(5, 0.1, SpawnType.SPHERE);
        } else {
            this.density = propertyMap.get(ParticlePropertyRegistry.DENSITY_PROPERTY.get());
        }
    }

    @Override
    public IParticleMotionType<?> getType() {
        return ParticleMotionRegistry.LIGHT_BLOB.get();
    }

    @Override
    public void tick(ParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        for(int i = 0; i < getNumParticles(density.density()); i++) {
            Vec3 adjustedVec = getMotionScaled(new Vec3(x, y, z), density.radius(), density.spawnType().orElse(SpawnType.SPHERE));
            level.addAlwaysVisibleParticle(particleOptions, true, adjustedVec.x, adjustedVec.y, adjustedVec.z, 0, 0, 0);
        }
    }

    @Override
    public List<Property<?>> getProperties() {
        return List.of(new ParticleDensityProperty(propertyMap, 20, 0.1)
                .minDensity(1)
                .maxDensity(200)
                .densityStepSize(1));
    }
}
