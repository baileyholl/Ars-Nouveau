package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.PropMap;
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

public class UpwardsMotion extends ParticleMotion{
    public static MapCodec<UpwardsMotion> CODEC = buildPropCodec(UpwardsMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, UpwardsMotion> STREAM = buildStreamCodec(UpwardsMotion::new);
    WallProperty wallProperty;

    public UpwardsMotion(PropMap propertyMap) {
        super(propertyMap);
        wallProperty = propertyMap.getOrDefault(ParticlePropertyRegistry.WALL_PROPERTY.get(), new WallProperty(5, 5, 20, Direction.NORTH));
    }

    public UpwardsMotion() {
        super(new PropMap());
        wallProperty = new WallProperty(5, 5, 20, Direction.NORTH);
    }

    @Override
    public void tick(ParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        if(particleOptions instanceof PropertyParticleOptions propertyParticleOptions) {
            wallProperty = propertyParticleOptions.map.getOrDefault(ParticlePropertyRegistry.WALL_PROPERTY.get(), new WallProperty(5, 5, 20, Direction.NORTH));
        }
        RandomSource rand = level.random;
        int chance = wallProperty.chance;
        int range = wallProperty.range;
        double growthFactor = 1.0f;
        Direction direction = wallProperty.direction;

        int numParticles = 20;

        BlockPos pos = BlockPos.containing(x, y, z);

        BlockPos.betweenClosedStream(pos.offset(range * direction.getStepX(), 0, range * direction.getStepZ()), pos.offset(-range  * direction.getStepX(), range, -range * direction.getStepZ())).forEach(blockPos -> {
            if (rand.nextInt(chance) == 0) {
                for (int i = 0; i < rand.nextInt(numParticles); i++) {
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
}
