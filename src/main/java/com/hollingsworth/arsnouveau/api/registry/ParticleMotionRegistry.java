package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.configurations.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ParticleMotionRegistry {
    public static final ResourceKey<Registry<IParticleMotionType<?>>> PARTICLE_CONFIG_REGISTRY_KEY = ResourceKey.createRegistryKey(ArsNouveau.prefix("particle_configs"));

    public static final Registry<IParticleMotionType<?>> PARTICLE_CONFIG_REGISTRY = new RegistryBuilder<>(PARTICLE_CONFIG_REGISTRY_KEY).sync(true).create();
    public static final DeferredRegister<IParticleMotionType<?>> PARTICLE_CONFIG = DeferredRegister.create(PARTICLE_CONFIG_REGISTRY, ArsNouveau.MODID);

    public static final DeferredHolder<IParticleMotionType<?>, IParticleMotionType<BurstMotion>> BURST_TYPE = PARTICLE_CONFIG.register("burst", () -> new SimpleParticleMotionType<>(BurstMotion.CODEC, BurstMotion.STREAM, BurstMotion::new));

    public static final DeferredHolder<IParticleMotionType<?>, IParticleMotionType<TrailMotion>> TRAIL_TYPE = PARTICLE_CONFIG.register("trail", () -> new SimpleParticleMotionType<>(TrailMotion.CODEC, TrailMotion.STREAM, TrailMotion::new));
    public static final DeferredHolder<IParticleMotionType<?>, IParticleMotionType<SpiralMotion>> SPIRAL_TYPE = PARTICLE_CONFIG.register("spiral", () -> new SimpleParticleMotionType<>(SpiralMotion.CODEC, SpiralMotion.STREAM, SpiralMotion::new));

    public static final DeferredHolder<IParticleMotionType<?>, IParticleMotionType<HelixMotion>> HELIX_TYPE = PARTICLE_CONFIG.register("helix", () -> new SimpleParticleMotionType<>(HelixMotion.CODEC, HelixMotion.STREAM, HelixMotion::new));


    public static final DeferredHolder<IParticleMotionType<?>, IParticleMotionType<UpwardsWallMotion>> UPWARD_WALL_TYPE = PARTICLE_CONFIG.register("upward_wall", () -> new SimpleParticleMotionType<>(UpwardsWallMotion.CODEC, UpwardsWallMotion.STREAM, UpwardsWallMotion::new));

    public static final DeferredHolder<IParticleMotionType<?>, IParticleMotionType<UpwardsFieldMotion>> UPWARD_FIELD_TYPE = PARTICLE_CONFIG.register("upward_field", () -> new SimpleParticleMotionType<>(UpwardsFieldMotion.CODEC, UpwardsFieldMotion.STREAM, UpwardsFieldMotion::new));

    public static final DeferredHolder<IParticleMotionType<?>, IParticleMotionType<NoneMotion>> NONE_TYPE = PARTICLE_CONFIG.register("none", () -> new SimpleParticleMotionType<>(NoneMotion.CODEC, NoneMotion.STREAM, NoneMotion::new));

    public static final DeferredHolder<IParticleMotionType<?>, IParticleMotionType<WaveMotion>> WAVE_TYPE = PARTICLE_CONFIG.register("wave", () -> new SimpleParticleMotionType<>(WaveMotion.CODEC, WaveMotion.STREAM, WaveMotion::new));
}
