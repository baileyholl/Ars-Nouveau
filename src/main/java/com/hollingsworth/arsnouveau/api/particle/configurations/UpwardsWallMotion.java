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

public class UpwardsWallMotion extends ParticleMotion {
    public static MapCodec<UpwardsWallMotion> CODEC = buildPropCodec(UpwardsWallMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, UpwardsWallMotion> STREAM = buildStreamCodec(UpwardsWallMotion::new);

    public UpwardsWallMotion(PropMap propertyMap) {
        super(propertyMap);
    }

    public UpwardsWallMotion() {
        this(new PropMap());
    }

    @Override
    public void tick(PropertyParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        WallProperty wallProperty = particleOptions.map.getOrDefault(ParticlePropertyRegistry.WALL_PROPERTY.get(), new WallProperty(5, 5, 20, Direction.NORTH));
        ParticleDensityProperty density = getDensity(particleOptions, 20, 0.0f);
        RandomSource rand = level.random;
        int chance = wallProperty.chance;
        int range = wallProperty.range;
        double growthFactor = 1.0f;
        Direction direction = wallProperty.direction;

        BlockPos pos = BlockPos.containing(x, y, z);

        BlockPos.betweenClosedStream(pos.offset(range * direction.getStepX(), 0, range * direction.getStepZ()), pos.offset(-range * direction.getStepX(), range, -range * direction.getStepZ())).forEach(blockPos -> {
            if (rand.nextInt(chance) == 0) {
                for (int i = 0; i < getNumParticles(density.density()); i++) {
                    double dx = blockPos.getX() + ParticleUtil.inRange(-growthFactor, growthFactor) + 0.5;
                    double dy = blockPos.getY() + ParticleUtil.inRange(-growthFactor, growthFactor) + 0.5;
                    double dz = blockPos.getZ() + ParticleUtil.inRange(-growthFactor, growthFactor) + 0.5;
                    level.addAlwaysVisibleParticle(particleOptions,
                            true,
                            dx, dy, dz,
                            0, ParticleUtil.inRange(0.01, 0.08), 0);
                }
            }
        });
    }

    @Override
    public IParticleMotionType<?> getType() {
        return ParticleMotionRegistry.UPWARD_WALL_TYPE.get();
    }

    @Override
    public List<BaseProperty<?>> getProperties(PropMap propMap) {
        return List.of(propMap.createIfMissing(new ParticleTypeProperty()),
                propMap.createIfMissing(new ParticleDensityProperty()
                        .minDensity(20)
                        .maxDensity(100)
                        .densityStepSize(10)
                        .supportsShapes(false))
        );
    }
}
