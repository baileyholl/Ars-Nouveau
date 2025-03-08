package com.hollingsworth.arsnouveau.api.particle;

import com.hollingsworth.arsnouveau.api.particle.configurations.IParticleType;
import com.hollingsworth.arsnouveau.api.registry.ParticleConfigRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public interface IParticleConfig {
    Codec<IParticleConfig> CODEC = ParticleConfigRegistry.PARTICLE_CONFIG_REGISTRY.byNameCodec().dispatch(IParticleConfig::getType, IParticleType::codec);

    StreamCodec<RegistryFriendlyByteBuf, IParticleConfig> STREAM_CODEC = ByteBufCodecs.registry(ParticleConfigRegistry.PARTICLE_CONFIG_REGISTRY_KEY).dispatch(IParticleConfig::getType, IParticleType::streamCodec);

    IParticleType<?> getType();

    void init(ParticleEmitter emitter);

    void tick(Level level, double x, double y, double z, double prevX, double prevY, double prevZ);

    default Component getName(){
        ResourceLocation key = ParticleConfigRegistry.PARTICLE_CONFIG_REGISTRY.getKey(this.getType());
        return Component.translatable(key.getNamespace() + ".particle_config." + key.getPath() + ".name");
    }
}
