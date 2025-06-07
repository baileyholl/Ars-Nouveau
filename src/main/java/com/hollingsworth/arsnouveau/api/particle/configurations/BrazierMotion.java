package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.*;
import com.hollingsworth.arsnouveau.api.registry.ParticleMotionRegistry;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

import java.util.List;

public class BrazierMotion extends ParticleMotion {

    public static MapCodec<BrazierMotion> CODEC = buildPropCodec(BrazierMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, BrazierMotion> STREAM =  buildStreamCodec(BrazierMotion::new);

    public BrazierMotion() {
        this(new PropMap());
    }

    public BrazierMotion(PropMap propertyMap) {
        super(propertyMap);
    }

    @Override
    public IParticleMotionType<?> getType() {
        return ParticleMotionRegistry.BRAZIER_TYPE.get();
    }

    @Override
    public void tick(PropertyParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        ParticleDensityProperty density = getDensity(particleOptions, 20, 0.1f);
        double xzOffset = 0.25;
        int intensity = 5;
        for (int i = 0; i < intensity; i++) {
            level.addParticle(
                    particleOptions,
                    x + ParticleUtil.inRange(-xzOffset / 2, xzOffset / 2), y + ParticleUtil.inRange(-0.05, 0.2), z + ParticleUtil.inRange(-xzOffset / 2, xzOffset / 2),
                    0, ParticleUtil.inRange(0.0, 0.05f), 0);
        }
        for (int i = 0; i < intensity; i++) {
            level.addParticle(
                    particleOptions,
                    x + ParticleUtil.inRange(-xzOffset, xzOffset), y + ParticleUtil.inRange(0, 0.7), z + ParticleUtil.inRange(-xzOffset, xzOffset),
                    0, ParticleUtil.inRange(0.0, 0.05f), 0);
        }
    }

    @Override
    public List<BaseProperty<?>> getProperties(PropMap propMap) {
        return List.of(new ParticleTypeProperty(propMap), new ParticleDensityProperty(propMap, 20, 0.1)
                .minDensity(1)
                .maxDensity(200)
                .densityStepSize(1), new SpeedProperty(propMap));
    }
}
