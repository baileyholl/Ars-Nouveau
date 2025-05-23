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
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BurstMotion extends ParticleMotion {

    public static MapCodec<BurstMotion> CODEC = buildPropCodec(BurstMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, BurstMotion> STREAM =  buildStreamCodec(BurstMotion::new);

    ParticleDensityProperty density;

    public BurstMotion() {
        this(new PropMap());
    }

    public BurstMotion(PropMap propertyMap) {
        super(propertyMap);
        if(!propertyMap.has(ParticlePropertyRegistry.DENSITY_PROPERTY.get())){
            this.density = new ParticleDensityProperty(5, 0.1, SpawnType.SPHERE);
        } else {
            this.density = propertyMap.get(ParticlePropertyRegistry.DENSITY_PROPERTY.get());
        }
    }

    @Override
    public IParticleMotionType<?> getType() {
        return ParticleMotionRegistry.BURST_TYPE.get();
    }

    @Override
    public void tick(ParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        Vec3 adjustedVec = getMotionScaled(new Vec3(x, y, z), density.radius(), density.spawnType().orElse(SpawnType.SPHERE));
        for (int i = 0; i < density.density(); i++) {
            level.addParticle(particleOptions, adjustedVec.x, adjustedVec.y, adjustedVec.z,
                    ParticleUtil.inRange(-0.05, 0.05),
                    ParticleUtil.inRange(0, 0.05),
                    ParticleUtil.inRange(-0.05, 0.05));
        }
    }

    @Override
    public List<Property<?>> getProperties() {
        return List.of(new ParticleDensityProperty(propertyMap, 5, 20, 1, true));
    }
}
