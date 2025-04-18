package com.hollingsworth.arsnouveau.api.particle;

import com.hollingsworth.arsnouveau.api.particle.configurations.properties.Property;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public class PropertyParticleOptions implements ParticleOptions, IConfigurableParticleOption {


    public static final MapCodec<PropertyParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BuiltInRegistries.PARTICLE_TYPE.byNameCodec().fieldOf("type").forGetter(PropertyParticleOptions::getType),
            ParticleColor.CODEC.fieldOf("color").forGetter(d -> d.color)
            )
            .apply(instance, PropertyParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PropertyParticleOptions> STREAM_CODEC = StreamCodec.of(
            PropertyParticleOptions::toNetwork, PropertyParticleOptions::fromNetwork
    );

    public static void toNetwork(RegistryFriendlyByteBuf buf, PropertyParticleOptions data) {
        ByteBufCodecs.registry(BuiltInRegistries.PARTICLE_TYPE.key()).encode(buf, data.type);
        ParticleColor.STREAM.encode(buf, data.color);
    }

    public static PropertyParticleOptions fromNetwork(RegistryFriendlyByteBuf buffer) {
        ParticleType<?> type =  ByteBufCodecs.registry(BuiltInRegistries.PARTICLE_TYPE.key()).decode(buffer);
        ParticleColor particleColor = ParticleColor.STREAM.decode(buffer);
        return new PropertyParticleOptions(type, particleColor);
    }

    public ParticleColor color;
    protected ParticleType<?> type;

    public PropertyParticleOptions(ParticleType<?> type, ParticleColor color) {
        this.type = type;
        this.color = color;
    }

    public PropertyParticleOptions(ParticleType<?> type) {
        this(type, ParticleColor.defaultParticleColor());
    }

    @Override
    public ParticleType<?> getType() {
        return type;
    }

    @Override
    public List<Property> getProperties() {
        return List.of();
    }
}
