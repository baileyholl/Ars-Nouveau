package com.hollingsworth.arsnouveau.api.particle;

import com.hollingsworth.arsnouveau.api.particle.configurations.IParticleType;
import com.hollingsworth.arsnouveau.api.registry.ParticleConfigRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

public interface IParticleCallback {
    Codec<IParticleCallback> CODEC = ParticleConfigRegistry.PARTICLE_CONFIG_REGISTRY.byNameCodec().dispatch(IParticleCallback::getType, IParticleType::codec);

    StreamCodec<RegistryFriendlyByteBuf, IParticleCallback> STREAM_CODEC = ByteBufCodecs.registry(ParticleConfigRegistry.PARTICLE_CONFIG_REGISTRY_KEY).dispatch(IParticleCallback::getType, IParticleType::streamCodec);

    IParticleType<?> getType();

    void tick(Level level, double x, double y, double z, double prevX, double prevY, double prevZ);
}
