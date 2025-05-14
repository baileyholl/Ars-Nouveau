package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.PropMap;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.Property;
import com.hollingsworth.arsnouveau.api.registry.ParticleConfigRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Spawns particles via callbacks from the emitter
 */
public abstract class ParticleMotion {
    public static Codec<ParticleMotion> CODEC = ParticleConfigRegistry.PARTICLE_CONFIG_REGISTRY.byNameCodec().dispatch(ParticleMotion::getType, IParticleMotionType::codec);

    public static StreamCodec<RegistryFriendlyByteBuf, ParticleMotion> STREAM_CODEC = ByteBufCodecs.registry(ParticleConfigRegistry.PARTICLE_CONFIG_REGISTRY_KEY).dispatch(ParticleMotion::getType, IParticleMotionType::streamCodec);

    public ParticleEmitter emitter;
    public PropMap propertyMap;

    public ParticleMotion(PropMap propertyMap){
        this.propertyMap = propertyMap;
    }


    public void init(ParticleEmitter emitter) {
        this.emitter = emitter;
    }

    public void tick(ParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {

    }

    public List<Property<?>> getProperties() {
        return List.of();
    }

    public abstract IParticleMotionType<?> getType();

    protected static <T extends ParticleMotion> MapCodec<T> buildPropCodec(Function<PropMap, T> constructor) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                PropMap.CODEC.fieldOf("propMap").forGetter(s -> s.propertyMap)
        ).apply(instance, constructor));
    }

    protected static <T extends ParticleMotion> StreamCodec<RegistryFriendlyByteBuf, T> buildStreamCodec(Function<PropMap, T> constructor){
        return new StreamCodec<>() {
            @Override
            public T decode(RegistryFriendlyByteBuf buffer) {
                return constructor.apply(PropMap.STREAM_CODEC.decode(buffer));
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buffer, T value) {
                PropMap.STREAM_CODEC.encode(buffer, value.propertyMap);

            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ParticleMotion that = (ParticleMotion) o;
        return Objects.equals(propertyMap, that.propertyMap);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(propertyMap);
    }


    public enum SpawnType {
        POINT,
        SPHERE,
        CUBE,
    }
}
