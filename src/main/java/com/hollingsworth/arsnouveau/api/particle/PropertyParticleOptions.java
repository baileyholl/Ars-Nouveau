package com.hollingsworth.arsnouveau.api.particle;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class PropertyParticleOptions extends ParticleType<PropertyParticleOptions> implements ParticleOptions {


    public static final MapCodec<PropertyParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ParticleColor.CODEC.fieldOf("color").forGetter(d -> d.color)
            )
            .apply(instance, PropertyParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PropertyParticleOptions> STREAM_CODEC = StreamCodec.of(
            PropertyParticleOptions::toNetwork, PropertyParticleOptions::fromNetwork
    );

    public static void toNetwork(RegistryFriendlyByteBuf buf, PropertyParticleOptions data) {
        ParticleColor.STREAM.encode(buf, data.color);
    }

    public static PropertyParticleOptions fromNetwork(RegistryFriendlyByteBuf buffer) {
        ParticleColor particleColor = ParticleColor.STREAM.decode(buffer);
        return new PropertyParticleOptions(particleColor);
    }


    protected ParticleType<? extends PropertyParticleOptions> type;
    public ParticleColor color;


    public PropertyParticleOptions(){
        this(ParticleColor.defaultParticleColor());
    }

    public PropertyParticleOptions(ParticleColor color) {
        super(false);
        this.color = color;
    }

    @Override
    public ParticleType<?> getType() {
        return this;
    }

    @Override
    public MapCodec<PropertyParticleOptions> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, PropertyParticleOptions> streamCodec() {
        return STREAM_CODEC;
    }
}
