package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.configurations.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ParticleConfigRegistry {
    public static final ResourceKey<Registry<IParticleConfigType<?>>> PARTICLE_CONFIG_REGISTRY_KEY = ResourceKey.createRegistryKey(ArsNouveau.prefix("particle_configs"));

    public static final Registry<IParticleConfigType<?>> PARTICLE_CONFIG_REGISTRY = new RegistryBuilder<>(PARTICLE_CONFIG_REGISTRY_KEY).sync(true).create();
    public static final DeferredRegister<IParticleConfigType<?>> PARTICLE_CONFIG = DeferredRegister.create(PARTICLE_CONFIG_REGISTRY, ArsNouveau.MODID);

    public static final DeferredHolder<IParticleConfigType<?>, IParticleConfigType<BurstConfiguration>> BURST_TYPE = PARTICLE_CONFIG.register("burst", () -> new SimpleParticleConfigType<>(BurstConfiguration.CODEC, BurstConfiguration.STREAM));

    public static final DeferredHolder<IParticleConfigType<?>, IParticleConfigType<TrailConfiguration>> TRAIL_TYPE = PARTICLE_CONFIG.register("trail", () -> new SimpleParticleConfigType<>(TrailConfiguration.CODEC, TrailConfiguration.STREAM));
    public static final DeferredHolder<IParticleConfigType<?>, IParticleConfigType<SpiralConfiguration>> SPIRAL_TYPE = PARTICLE_CONFIG.register("spiral", () -> new SimpleParticleConfigType<>(SpiralConfiguration.CODEC, SpiralConfiguration.STREAM));

    public static final DeferredHolder<IParticleConfigType<?>, IParticleConfigType<HelixConfiguration>> HELIX_TYPE = PARTICLE_CONFIG.register("helix", () -> new SimpleParticleConfigType<>(HelixConfiguration.CODEC, HelixConfiguration.STREAM));
}
