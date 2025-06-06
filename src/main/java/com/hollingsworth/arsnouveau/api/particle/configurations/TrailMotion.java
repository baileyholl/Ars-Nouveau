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
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class TrailMotion extends ParticleMotion {

    public static MapCodec<TrailMotion> CODEC = buildPropCodec(TrailMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, TrailMotion> STREAM = buildStreamCodec(TrailMotion::new);

    public TrailMotion(PropMap propMap){
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
        int density = densityProp.density();
        double radius = densityProp.radius();
        SpawnType spawnType = densityProp.spawnType().orElse(SpawnType.SPHERE);
        RandomSource random = level.random;
        int totalParticles = getNumParticles(density);
        double deltaX = x - prevX;
        double deltaY = y - prevY;
        double deltaZ = z - prevZ;

        for (int i = 0; i < totalParticles; i++) {
            double t = (double) i / Math.max(1, totalParticles - 1);
            double px = prevX + deltaX * t;
            double py = prevY + deltaY * t;
            double pz = prevZ + deltaZ * t;
            Vec3 deltaVec =  new Vec3(px, py, pz);
            Vec3 point = getMotionScaled(deltaVec, radius, spawnType);
            level.addAlwaysVisibleParticle(
                    particleOptions,
                    point.x,
                    point.y,
                    point.z,
                    0.0125f * (random.nextFloat() - 0.5f),
                    0.0125f * (random.nextFloat() - 0.5f),
                    0.0125f * (random.nextFloat() - 0.5f));
        }
    }

    @Override
    public List<BaseProperty<?>> getProperties(PropMap propMap) {
        return List.of(new ParticleTypeProperty(propMap), new ParticleDensityProperty(propMap, 100, 0.1f));
    }
}
