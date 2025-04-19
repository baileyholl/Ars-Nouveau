package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.registry.ParticleConfigRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

public class BurstMotion extends ParticleMotion {
    public static BurstMotion INSTANCE = new BurstMotion();
    public static MapCodec<BurstMotion> CODEC = MapCodec.unit(BurstMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, BurstMotion> STREAM = new StreamCodec<RegistryFriendlyByteBuf, BurstMotion>() {
        @Override
        public BurstMotion decode(RegistryFriendlyByteBuf buffer) {
            return new BurstMotion();
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, BurstMotion value) {

        }
    };

    @Override
    public IParticleMotionType<?> getType() {
        return ParticleConfigRegistry.BURST_TYPE.get();
    }

    @Override
    public void tick(ParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        for (int i = 0; i < 10; i++) {
            double d0 = x + 0.5;
            double d1 = y + 1.2;
            double d2 = z + .5;
            level.addParticle(particleOptions, d0, d1, d2,
                    (level.random.nextFloat() - 0.5) / 3.0,
                    (level.random.nextFloat() - 0.5) / 3.0,
                    (level.random.nextFloat() - 0.5) / 3.0);
        }
    }
}
