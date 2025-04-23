package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.configurations.properties.PropMap;
import com.hollingsworth.arsnouveau.api.registry.ParticleConfigRegistry;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

public class BurstMotion extends ParticleMotion {

    public static MapCodec<BurstMotion> CODEC = buildPropCodec(BurstMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, BurstMotion> STREAM =  buildStreamCodec(BurstMotion::new);

    public BurstMotion() {
        super(new PropMap());
    }

    public BurstMotion(PropMap propertyMap) {
        super(propertyMap);
    }

    @Override
    public IParticleMotionType<?> getType() {
        return ParticleConfigRegistry.BURST_TYPE.get();
    }

    @Override
    public void tick(ParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        for (int i = 0; i < 10; i++) {
            double d0 = x;
            double d1 = y;
            double d2 = z;
            level.addParticle(particleOptions, d0, d1, d2,
                    ParticleUtil.inRange(-0.05, 0.05),
                    ParticleUtil.inRange(-0.05, 0.05),
                    ParticleUtil.inRange(-0.05, 0.05));
        }
    }
}
