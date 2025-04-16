package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ColorProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.IParticleProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.TextureProperty;
import com.hollingsworth.arsnouveau.api.registry.ParticleConfigRegistry;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import java.util.List;

public class TrailConfiguration extends ParticleConfiguration {

    public static MapCodec<TrailConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ParticleTypes.CODEC.fieldOf("particleOptions").forGetter(i -> i.particleOptions)
    ).apply(instance, TrailConfiguration::new));


    public static StreamCodec<RegistryFriendlyByteBuf, TrailConfiguration> STREAM = StreamCodec.composite(
            ParticleTypes.STREAM_CODEC,
            ParticleConfiguration::particleOptions,
            TrailConfiguration::new
    );


    public TrailConfiguration(ParticleOptions particleOptions) {
        super(particleOptions);
    }

    public TrailConfiguration(){
        this(GlowParticleData.createData(ParticleColor.defaultParticleColor()));
    }

    @Override
    public IConfigurableParticleType<?> getType() {
        return ParticleConfigRegistry.TRAIL_TYPE.get();
    }

    @Override
    public void tick(Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        RandomSource random = level.random;
        double deltaX = x - prevX;
        double deltaY = y - prevY;
        double deltaZ = z - prevZ;
        double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 6);
        for (double j = 0; j < dist; j++) {
            double coeff = j / dist;
            level.addParticle(this.particleOptions,
                    (float) (prevX + deltaX * coeff),
                    (float) (prevY + deltaY * coeff) + 0.1, (float)
                            (prevZ + deltaZ * coeff),
                    0.0125f * (random.nextFloat() - 0.5f),
                    0.0125f * (random.nextFloat() - 0.5f),
                    0.0125f * (random.nextFloat() - 0.5f));
        }
    }

    @Override
    public List<IParticleProperty> getProperties() {
        return List.of(new TextureProperty((texture) -> {}), new ColorProperty((color) -> {}));
    }
}
