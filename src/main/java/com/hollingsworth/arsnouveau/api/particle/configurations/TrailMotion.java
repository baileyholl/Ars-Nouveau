package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.registry.ParticleConfigRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class TrailMotion extends ParticleMotion {
    public static TrailMotion INSTANCE = new TrailMotion();
    public static MapCodec<TrailMotion> CODEC = MapCodec.unit(TrailMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, TrailMotion> STREAM = new StreamCodec<>() {
        @Override
        public TrailMotion decode(RegistryFriendlyByteBuf buffer) {
            return new TrailMotion();
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, TrailMotion value) {

        }
    };


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
        for (double j = 0; j < dist; j++) {
            double coeff = j / dist;
            level.addParticle(particleOptions,
                    (float) (prevX + deltaX * coeff),
                    (float) (prevY + deltaY * coeff) + 0.1, (float)
                            (prevZ + deltaZ * coeff),
                    0.0125f * (random.nextFloat() - 0.5f),
                    0.0125f * (random.nextFloat() - 0.5f),
                    0.0125f * (random.nextFloat() - 0.5f));
        }
    }
}
