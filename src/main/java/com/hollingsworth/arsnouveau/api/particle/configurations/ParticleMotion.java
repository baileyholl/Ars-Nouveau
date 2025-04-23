package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.Property;
import com.hollingsworth.arsnouveau.api.registry.ParticleConfigRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Spawns particles via callbacks from the emitter
 */
public abstract class ParticleMotion {
    public static Codec<ParticleMotion> CODEC = ParticleConfigRegistry.PARTICLE_CONFIG_REGISTRY.byNameCodec().dispatch(ParticleMotion::getType, IParticleMotionType::codec);

    public static StreamCodec<RegistryFriendlyByteBuf, ParticleMotion> STREAM_CODEC = ByteBufCodecs.registry(ParticleConfigRegistry.PARTICLE_CONFIG_REGISTRY_KEY).dispatch(ParticleMotion::getType, IParticleMotionType::streamCodec);

    public ParticleEmitter emitter;


    public void init(ParticleEmitter emitter) {
        this.emitter = emitter;
    }

    public void tick(ParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {

    }

    public List<Property> getProperties() {
        return List.of();
    }

    public abstract IParticleMotionType<?> getType();


}
