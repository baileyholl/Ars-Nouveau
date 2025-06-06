package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.*;
import com.hollingsworth.arsnouveau.api.registry.ParticleMotionRegistry;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import java.util.List;

public class UpwardsFieldMotion extends ParticleMotion{
    public static MapCodec<UpwardsFieldMotion> CODEC = buildPropCodec(UpwardsFieldMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, UpwardsFieldMotion> STREAM = buildStreamCodec(UpwardsFieldMotion::new);

    public UpwardsFieldMotion(PropMap propertyMap) {
        super(propertyMap);
    }

    public UpwardsFieldMotion() {
        this(new PropMap());
    }

    @Override
    public void tick(PropertyParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        WallProperty wallProperty = particleOptions.map.getOrDefault(ParticlePropertyRegistry.WALL_PROPERTY.get(), new WallProperty(5, 5, 20, Direction.NORTH));
        ParticleDensityProperty density = getDensity(particleOptions);
        RandomSource rand = level.random;
        int chance = wallProperty.chance;
        int range = wallProperty.range;

        BlockPos pos = BlockPos.containing(x, y + 1, z);

        BlockPos.betweenClosedStream(pos.offset(range, 0, range), pos.offset(-range, 0, -range)).forEach(blockPos -> {
            if (rand.nextInt(chance) == 0) {
                for (int i = 0; i < getNumParticles(density.density()); i++) {
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
    public List<BaseProperty<?>> getProperties(PropMap propMap) {
        return List.of(new ParticleTypeProperty(propMap), new ParticleDensityProperty(propMap, 20, 100, 10,false));
    }
}
