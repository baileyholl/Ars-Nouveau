package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.BaseProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ParticleDensityProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.PropMap;
import com.hollingsworth.arsnouveau.api.registry.ParticleMotionRegistry;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Spawns particles via callbacks from the emitter
 */
public abstract class ParticleMotion {
    public static Codec<ParticleMotion> CODEC = ParticleMotionRegistry.PARTICLE_CONFIG_REGISTRY.byNameCodec().dispatch(ParticleMotion::getType, IParticleMotionType::codec);

    public static StreamCodec<RegistryFriendlyByteBuf, ParticleMotion> STREAM_CODEC = ByteBufCodecs.registry(ParticleMotionRegistry.PARTICLE_CONFIG_REGISTRY_KEY).dispatch(ParticleMotion::getType, IParticleMotionType::streamCodec);

    public ParticleEmitter emitter;
    public PropMap propertyMap;

    public ParticleMotion(PropMap propertyMap){
        this.propertyMap = propertyMap;
    }


    public void init(ParticleEmitter emitter) {
        this.emitter = emitter;
    }

    public abstract void tick(PropertyParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ);

    public List<BaseProperty<?>> getProperties(PropMap propMap) {
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
        return Objects.hash(ParticleMotionRegistry.PARTICLE_CONFIG_REGISTRY.getKey(getType()), propertyMap);
    }

    public Vec3 getMotionScaled(Vec3 deltaVec, double radius, SpawnType spawnType){
        return switch (spawnType){
            case SPHERE -> deltaVec.add(ParticleUtil.pointInSphere().scale(radius));
            case CUBE -> deltaVec.add(ParticleUtil.pointInCube().scale(radius));
        };
    }

    public int getNumParticles(int particlesSec){
        if(particlesSec == 0)
            return 0;
        double spawnRateTick = particlesSec * 0.05;
        var modulo = Math.round(1/ spawnRateTick);
        float ceilFloor = this.emitter.age;
        if (modulo != 0 && ceilFloor % modulo == 0) {
            spawnRateTick = Math.ceil(spawnRateTick);
        } else {
            spawnRateTick = Math.floor(spawnRateTick);
        }
        return (int) spawnRateTick;
    }

    public Vector3f toEmitterSpace(float x, float y, float z, float localX, float localY, float localZ) {
        float xRotRadians = (float) Math.toRadians(this.emitter.getRotation().x);
        float yRotRadians = (float) Math.toRadians(this.emitter.getRotation().y);
        Matrix4f transform = new Matrix4f();
        transform.identity()
                .translate(new Vector3f(x, y, z))
                .rotateY(yRotRadians)
                .rotateX(-xRotRadians);

        Vector3f localPos = new Vector3f(localX, localY, localZ);
        transform.transformPosition(localPos);
        return localPos;
    }

    protected ParticleDensityProperty getDensity(PropertyParticleOptions particleOptions, int defaultDensity, float defaultRadius) {
        return particleOptions.map.getOrDefault(ParticlePropertyRegistry.DENSITY_PROPERTY.get(), new ParticleDensityProperty(defaultDensity, defaultRadius, SpawnType.SPHERE));
    }

    protected ParticleDensityProperty getDensity(PropertyParticleOptions particleOptions) {
        return particleOptions.map.getOrDefault(ParticlePropertyRegistry.DENSITY_PROPERTY.get(), new ParticleDensityProperty());
    }

    public enum SpawnType {
        SPHERE,
        CUBE,
    }
}
