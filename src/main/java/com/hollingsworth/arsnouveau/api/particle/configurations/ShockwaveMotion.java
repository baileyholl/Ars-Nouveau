package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.BaseProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ParticleDensityProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ParticleTypeProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.PropMap;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.SpeedProperty;
import com.hollingsworth.arsnouveau.api.registry.ParticleMotionRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ShockwaveMotion extends ParticleMotion {

    public static final MapCodec<ShockwaveMotion> CODEC = buildPropCodec(ShockwaveMotion::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, ShockwaveMotion> STREAM = buildStreamCodec(ShockwaveMotion::new);

    public ShockwaveMotion() {
        this(new PropMap());
    }

    public ShockwaveMotion(PropMap propertyMap) {
        super(propertyMap);
    }

    @Override
    public void tick(PropertyParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        ParticleDensityProperty density = getDensity(particleOptions, 100, 0.1f);
        double waveRadius = emitter.age + 1;
        int particleCount = Math.max(12, (int) Math.ceil(waveRadius * Math.PI * 2.0 * density.density() / 100.0));
        for (int i = 0; i < particleCount; i++) {
            double angle = Math.PI * 2.0 * i / particleCount;
            Vec3 point = getMotionScaled(new Vec3(x + Math.cos(angle) * waveRadius, y, z + Math.sin(angle) * waveRadius),
                    density.radius(), density.spawnType().orElse(SpawnType.SPHERE));
            Vec3 speed = randomSpeed(particleOptions, -0.01, 0.01, 0.0, 0.03);
            level.addAlwaysVisibleParticle(particleOptions, true, point.x, point.y, point.z, speed.x, speed.y, speed.z);
        }
    }

    @Override
    public IParticleMotionType<?> getType() {
        return ParticleMotionRegistry.SHOCKWAVE_TYPE.get();
    }

    @Override
    public List<BaseProperty<?>> getProperties(PropMap propMap) {
        return List.of(propMap.createIfMissing(new ParticleTypeProperty()),
                propMap.createIfMissing(new ParticleDensityProperty(100, 0.1f, SpawnType.SPHERE)
                        .minDensity(20)
                        .maxDensity(200)
                        .densityStepSize(5)
                        .supportsShapes(true)
                        .minRadius(0.05)
                        .maxRadius(0.5)),
                propMap.createIfMissing(new SpeedProperty(0.0, 0.03, -0.01, 0.01)));
    }
}
