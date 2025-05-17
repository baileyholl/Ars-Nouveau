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

import java.util.List;

public class BurstMotion extends ParticleMotion {

    public static MapCodec<BurstMotion> CODEC = buildPropCodec(BurstMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, BurstMotion> STREAM =  buildStreamCodec(BurstMotion::new);

    public BurstMotion() {
        super(new PropMap());
    }
    int density = 5;

    public BurstMotion(PropMap propertyMap) {
        super(propertyMap);
        if(!propertyMap.has(ParticlePropertyRegistry.DENSITY_PROPERTY.get())){
            this.density = 5;
        } else {
            this.density = propertyMap.get(ParticlePropertyRegistry.DENSITY_PROPERTY.get()).density();
        }
    }

    @Override
    public IParticleMotionType<?> getType() {
        return ParticleMotionRegistry.BURST_TYPE.get();
    }

    @Override
    public void tick(ParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        for (int i = 0; i < density; i++) {
            double d0 = x;
            double d1 = y;
            double d2 = z;
            level.addParticle(particleOptions, d0, d1, d2,
                    ParticleUtil.inRange(-0.05, 0.05),
                    ParticleUtil.inRange(0, 0.05),
                    ParticleUtil.inRange(-0.05, 0.05));
        }
    }

    @Override
    public List<Property<?>> getProperties() {
        return List.of(new ParticleDensityProperty(propertyMap));
    }
}
