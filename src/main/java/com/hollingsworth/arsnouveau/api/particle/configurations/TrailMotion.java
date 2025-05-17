package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class TrailMotion extends ParticleMotion {

    public static MapCodec<TrailMotion> CODEC = buildPropCodec(TrailMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, TrailMotion> STREAM = buildStreamCodec(TrailMotion::new);

    public int density;
    public SpawnType spawnType;
    public double radius;

    public TrailMotion(PropMap propMap){
        super(propMap);
        if(!propMap.has(ParticlePropertyRegistry.DENSITY_PROPERTY.get())){
            this.density = 100;
            this.radius = 0.1;
            this.spawnType = SpawnType.SPHERE;
        } else {
            ParticleDensityProperty densityProperty = propMap.get(ParticlePropertyRegistry.DENSITY_PROPERTY.get());
            this.radius = densityProperty.radius();
            this.density = densityProperty.density();
            this.spawnType = densityProperty.spawnType().orElse(SpawnType.SPHERE);

        }
    }

    public TrailMotion() {
        super(new PropMap());
        this.density = 100;
        radius = 0.1;
        propertyMap.set(ParticlePropertyRegistry.DENSITY_PROPERTY.get(), new ParticleDensityProperty(density, radius, SpawnType.SPHERE));
        spawnType = SpawnType.SPHERE;
    }


    @Override
    public IParticleMotionType<?> getType() {
        return ParticleMotionRegistry.TRAIL_TYPE.get();
    }

    @Override
    public void tick(ParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        RandomSource random = level.random;
        int totalParticles = getNumParticles(particleOptions);

        double deltaX = x - prevX;
        double deltaY = y - prevY;
        double deltaZ = z - prevZ;

        for (int i = 0; i < totalParticles; i++) {
            double t = (double) i / Math.max(1, totalParticles - 1); // evenly spaced 0..1
            double px = prevX + deltaX * t;
            double py = prevY + deltaY * t;
            double pz = prevZ + deltaZ * t;
            Vec3 deltaVec =  new Vec3(px, py, pz);
            Vec3 point = switch (spawnType){
                case SPHERE -> deltaVec.add(ParticleUtil.pointInSphere().scale(radius));
                case CUBE -> deltaVec.add(ParticleUtil.pointInCube().scale(radius));
            };
            level.addParticle(
                    particleOptions,
                    point.x,
                    point.y,
                    point.z,
                    0.0125f * (random.nextFloat() - 0.5f),
                    0.0125f * (random.nextFloat() - 0.5f),
                    0.0125f * (random.nextFloat() - 0.5f));
        }
    }
    /*
        Returns a random point of a sphere, evenly distributed over the sphere.
        The sphere is centered at (x0,y0,z0) with the passed in radius.
    */
    public Vec3 randomSpherePoint(double x0,double y0, double z0, double radius){
        double u = Math.random();
        double v = Math.random();
        double theta = 2 * Math.PI * u;
        double phi = Math.acos(2 * v - 1);
        double x = x0 + (radius * Math.sin(phi) * Math.cos(theta));
        double y = y0 + (radius * Math.sin(phi) * Math.sin(theta));
        double z = z0 + (radius * Math.cos(phi));
        return new Vec3(x, y, z);
    }

    public int getNumParticles(ParticleOptions particleOptions){
        if(!(particleOptions instanceof PropertyParticleOptions propertyParticleOptions)){
            return 5;
        }
        double spawnRateTick = density * 0.05;

        var modulo = Math.round(1/spawnRateTick);
        float ceilFloor = propertyParticleOptions.map.getOptional(ParticlePropertyRegistry.EMITTER_PROPERTY.get()).get().age;
        if (modulo != 0 && ceilFloor % modulo == 0) {
            spawnRateTick = Math.ceil(spawnRateTick);
        } else {
            spawnRateTick = Math.floor(spawnRateTick);
        }
        return (int) spawnRateTick;
    }

    @Override
    public List<Property<?>> getProperties() {
        return List.of(new ParticleDensityProperty(propertyMap));
    }
}
