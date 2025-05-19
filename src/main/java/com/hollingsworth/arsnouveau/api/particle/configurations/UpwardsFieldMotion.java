package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ParticleDensityProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.PropMap;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.Property;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.WallProperty;
import com.hollingsworth.arsnouveau.api.registry.ParticleMotionRegistry;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import java.util.List;

public class UpwardsFieldMotion extends ParticleMotion{
    public static MapCodec<UpwardsFieldMotion> CODEC = buildPropCodec(UpwardsFieldMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, UpwardsFieldMotion> STREAM = buildStreamCodec(UpwardsFieldMotion::new);
    WallProperty wallProperty;
    ParticleDensityProperty density;
    public UpwardsFieldMotion(PropMap propertyMap) {
        super(propertyMap);
        wallProperty = propertyMap.getOrDefault(ParticlePropertyRegistry.WALL_PROPERTY.get(), new WallProperty(5, 5, 20, Direction.NORTH));
        if(!propertyMap.has(ParticlePropertyRegistry.DENSITY_PROPERTY.get())){
            density = new ParticleDensityProperty(5, 0, SpawnType.SPHERE);
        } else {
            density = propertyMap.get(ParticlePropertyRegistry.DENSITY_PROPERTY.get());
        }
    }

    public UpwardsFieldMotion() {
        this(new PropMap());
    }

    @Override
    public void tick(ParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        if(particleOptions instanceof PropertyParticleOptions propertyParticleOptions) {
            wallProperty = propertyParticleOptions.map.getOrDefault(ParticlePropertyRegistry.WALL_PROPERTY.get(), new WallProperty(5, 5, 20, Direction.NORTH));
        }
        RandomSource rand = level.random;
        int chance = wallProperty.chance;
        int range = wallProperty.range;

        BlockPos pos = BlockPos.containing(x, y + 1, z);

        BlockPos.betweenClosedStream(pos.offset(range, 0, range), pos.offset(-range, 0, -range)).forEach(blockPos -> {
            if (rand.nextInt(chance) == 0) {
                for (int i = 0; i < getNumParticles(particleOptions, density.density()); i++) {
                    double dx = blockPos.getX() + ParticleUtil.inRange(-0.5, 0.5) + 0.5;
                    double dy = blockPos.getY() + ParticleUtil.inRange(-0.01, 0.25);
                    double dz = blockPos.getZ() + ParticleUtil.inRange(-0.5, 0.5) + 0.5;
                    level.addAlwaysVisibleParticle(particleOptions, true, dx, dy, dz, 0, ParticleUtil.inRange(0.01, 0.08), 0);
                }
            }
        });
    }

    @Override
    public IParticleMotionType<?> getType() {
        return ParticleMotionRegistry.UPWARD_FIELD_TYPE.get();
    }

    @Override
    public List<Property<?>> getProperties() {
        return List.of(new ParticleDensityProperty(propertyMap, 20, 100, 10,false));
    }
}
