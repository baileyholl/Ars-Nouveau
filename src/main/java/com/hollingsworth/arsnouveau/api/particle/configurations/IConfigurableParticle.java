package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.IParticleProperty;
import com.hollingsworth.arsnouveau.api.registry.ParticleConfigRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

import java.util.List;

public interface IConfigurableParticle {
    Codec<IConfigurableParticle> CODEC = ParticleConfigRegistry.PARTICLE_CONFIG_REGISTRY.byNameCodec().dispatch(IConfigurableParticle::getType, IConfigurableParticleType::codec);

    StreamCodec<RegistryFriendlyByteBuf, IConfigurableParticle> STREAM_CODEC = ByteBufCodecs.registry(ParticleConfigRegistry.PARTICLE_CONFIG_REGISTRY_KEY).dispatch(IConfigurableParticle::getType, IConfigurableParticleType::streamCodec);

    IConfigurableParticleType<?> getType();

    void init(ParticleEmitter emitter);

    void tick(Level level, double x, double y, double z, double prevX, double prevY, double prevZ);

    default List<IParticleProperty> getProperties() {
        return List.of();
    }
}
