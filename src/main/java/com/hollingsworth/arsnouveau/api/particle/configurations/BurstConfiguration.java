package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.registry.ParticleConfigRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

public class BurstConfiguration extends ParticleConfiguration{

    public static MapCodec<BurstConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ParticleTypes.CODEC.fieldOf("particleOptions").forGetter(i -> i.particleOptions)
    ).apply(instance, BurstConfiguration::new));

    public static StreamCodec<RegistryFriendlyByteBuf, BurstConfiguration> STREAM = StreamCodec.composite(
            ParticleTypes.STREAM_CODEC,
            ParticleConfiguration::particleOptions,
            BurstConfiguration::new
    );

    public BurstConfiguration(ParticleOptions particleOptions) {
        super(particleOptions);
    }


    @Override
    public IParticleType<?> getType() {
        return ParticleConfigRegistry.BURST_TYPE.get();
    }

    @Override
    public void tick(Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        for (int i = 0; i < 10; i++) {
            double d0 = x + 0.5;
            double d1 = y + 1.2;
            double d2 = z + .5;
            level.addParticle(this.particleOptions, d0, d1, d2,
                    (level.random.nextFloat() - 0.5) / 3.0,
                    (level.random.nextFloat() - 0.5) / 3.0,
                    (level.random.nextFloat() - 0.5) / 3.0);
        }
    }
}
