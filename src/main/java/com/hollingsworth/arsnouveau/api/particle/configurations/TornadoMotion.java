package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ParticleDensityProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.PropMap;
import com.hollingsworth.arsnouveau.api.registry.ParticleMotionRegistry;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

public class TornadoMotion extends ParticleMotion {
    public static MapCodec<TornadoMotion> CODEC = buildPropCodec(TornadoMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, TornadoMotion> STREAM = buildStreamCodec(TornadoMotion::new);


    public TornadoMotion(PropMap propMap) {
        super(propMap);
    }

    @Override
    public void tick(PropertyParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        ParticleDensityProperty density = getDensity(particleOptions);
        int spiralLayers = 10;
        int pointsPerLayer = 3;
        double swirlRadius = 2;
        double coneHeight = 2.0;
        double taper = 0.8;
        float randomScale = 0.02f;

        double rotationSpeed = 0.3;
        double spinOffset = emitter.age * rotationSpeed;
        for (double layer = 0; layer < spiralLayers; layer += 1) {
            double progress = layer / spiralLayers;
            double radius = swirlRadius * (progress * taper);

            for (int point = 0; point < pointsPerLayer; point++) {
                double baseAngle = 2 * Math.PI * point / pointsPerLayer;
                double angle = baseAngle + spinOffset;
                level.addParticle(particleOptions, x + Math.cos(angle) * radius, y + progress * coneHeight, z + Math.sin(angle) * radius,
                        ParticleUtil.inRange(-randomScale, randomScale),
                        ParticleUtil.inRange(-randomScale, randomScale),
                        ParticleUtil.inRange(-randomScale, randomScale));
            }
        }

    }

    @Override
    public IParticleMotionType<?> getType() {
        return ParticleMotionRegistry.TORNADO_TYPE.get();
    }
}
