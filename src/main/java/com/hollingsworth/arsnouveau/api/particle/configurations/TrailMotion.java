package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ParticleDensityProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.PropMap;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.Property;
import com.hollingsworth.arsnouveau.api.registry.ParticleConfigRegistry;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
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

    public TrailMotion(PropMap propMap){
        super(propMap);
        if(!propMap.has(ParticlePropertyRegistry.DENSITY_PROPERTY.get())){
            this.density = 5;
        } else {
            this.density = propMap.get(ParticlePropertyRegistry.DENSITY_PROPERTY.get()).density;
        }
    }

    public TrailMotion() {
        super(new PropMap());
        this.density = 5;
        propertyMap.set(ParticlePropertyRegistry.DENSITY_PROPERTY.get(), new ParticleDensityProperty(density));
    }


    @Override
    public IParticleMotionType<?> getType() {
        return ParticleConfigRegistry.TRAIL_TYPE.get();
    }

    @Override
    public void tick(ParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        RandomSource random = level.random;
        double deltaX = x - prevX;
        double deltaY = y - prevY;
        double deltaZ = z - prevZ;
        double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 6);
//        for (double j = 0; j < dist; j++) {
//            double coeff = j / dist;
//            level.addParticle(ParticleTypes.SCULK_SOUL,
//                    (float) (prevX + deltaX * coeff),
//                    (float) (prevY + deltaY * coeff) + 0.1, (float)
//                            (prevZ + deltaZ * coeff),
//                    0.0125f * (random.nextFloat() - 0.5f),
//                    0.0125f * (random.nextFloat() - 0.5f),
//                    0.0125f * (random.nextFloat() - 0.5f));
//        }
        for (double j = 0; j < dist; j++) {
            double coeff = j / dist;
            for (int i = 0; i < density; i++) {
                Vec3 point = randomSpherePoint(x + deltaX * coeff, y + deltaY * coeff, z + deltaZ * coeff, 0.1);
                level.addParticle(particleOptions,
                        point.x,
                        point.y,
                        point.z,
                        0.0125f * (random.nextFloat() - 0.5f),
                        0.0125f * (random.nextFloat() - 0.5f),
                        0.0125f * (random.nextFloat() - 0.5f));
            }
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

    @Override
    public List<Property> getProperties() {
        return List.of(new ParticleDensityProperty(propertyMap));
    }
}
