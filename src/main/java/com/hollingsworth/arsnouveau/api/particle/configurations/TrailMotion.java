package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.*;
import com.hollingsworth.arsnouveau.api.registry.ParticleMotionRegistry;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class TrailMotion extends ParticleMotion {

    public static MapCodec<TrailMotion> CODEC = buildPropCodec(TrailMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, TrailMotion> STREAM = buildStreamCodec(TrailMotion::new);

    public TrailMotion(PropMap propMap) {
        super(propMap);
    }

    public TrailMotion() {
        super(new PropMap());
    }


    @Override
    public IParticleMotionType<?> getType() {
        return ParticleMotionRegistry.TRAIL_TYPE.get();
    }

    @Override
    public void tick(PropertyParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        ParticleDensityProperty densityProp = getDensity(particleOptions, 100, 0.1f);
        SpeedProperty speedProperty = getSpeed(particleOptions);
        double minXSpeed = speedProperty.minXZ() * 0.25f;
        double minYSpeed = speedProperty.minY() * 0.25f;
        double maxXSpeed = speedProperty.maxXZ() * 0.25f;
        double maxYSpeed = speedProperty.maxY() * 0.25f;

        int density = densityProp.density();
        double radius = densityProp.radius();
        SpawnType spawnType = densityProp.spawnType().orElse(SpawnType.SPHERE);

        int totalParticles = getNumParticles(density);
        double deltaX = x - prevX;
        double deltaY = y - prevY;
        double deltaZ = z - prevZ;
        for (int i = 0; i < totalParticles; i++) {
            double t = (double) i / Math.max(1, totalParticles - 1);
            double px = prevX + deltaX * t;
            double py = prevY + deltaY * t;
            double pz = prevZ + deltaZ * t;
            Vec3 deltaVec = new Vec3(px, py, pz);
            Vec3 point = getMotionScaled(deltaVec, radius, spawnType);
            level.addAlwaysVisibleParticle(
                    particleOptions,
                    true,
                    point.x,
                    point.y,
                    point.z,
                    ParticleUtil.inRange(minXSpeed, maxXSpeed),
                    ParticleUtil.inRange(minYSpeed, maxYSpeed),
                    ParticleUtil.inRange(minXSpeed, maxXSpeed));
        }
    }

    @Override
    public List<BaseProperty<?>> getProperties(PropMap propMap) {
        return List.of(propMap.createIfMissing(new ParticleTypeProperty()), propMap.createIfMissing(new ParticleDensityProperty(100, 0.1f, SpawnType.SPHERE)));
    }
}
