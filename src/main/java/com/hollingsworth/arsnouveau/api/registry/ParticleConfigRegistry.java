package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.configurations.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ParticleConfigRegistry {
    public static final ResourceKey<Registry<IConfigurableParticleType<?>>> PARTICLE_CONFIG_REGISTRY_KEY = ResourceKey.createRegistryKey(ArsNouveau.prefix("particle_configs"));

    public static final Registry<IConfigurableParticleType<?>> PARTICLE_CONFIG_REGISTRY = new RegistryBuilder<>(PARTICLE_CONFIG_REGISTRY_KEY).sync(true).create();
    public static final DeferredRegister<IConfigurableParticleType<?>> PARTICLE_CONFIG = DeferredRegister.create(PARTICLE_CONFIG_REGISTRY, ArsNouveau.MODID);

    public static final DeferredHolder<IConfigurableParticleType<?>, IConfigurableParticleType<BurstConfiguration>> BURST_TYPE = PARTICLE_CONFIG.register("burst", () -> new SimpleConfigurableParticleType<>(BurstConfiguration.CODEC, BurstConfiguration.STREAM, BurstConfiguration::new));

    public static final DeferredHolder<IConfigurableParticleType<?>, IConfigurableParticleType<TrailConfiguration>> TRAIL_TYPE = PARTICLE_CONFIG.register("trail", () -> new SimpleConfigurableParticleType<>(TrailConfiguration.CODEC, TrailConfiguration.STREAM, TrailConfiguration::new));
    public static final DeferredHolder<IConfigurableParticleType<?>, IConfigurableParticleType<SpiralConfiguration>> SPIRAL_TYPE = PARTICLE_CONFIG.register("spiral", () -> new SimpleConfigurableParticleType<>(SpiralConfiguration.CODEC, SpiralConfiguration.STREAM, SpiralConfiguration::new));

    public static final DeferredHolder<IConfigurableParticleType<?>, IConfigurableParticleType<HelixConfiguration>> HELIX_TYPE = PARTICLE_CONFIG.register("helix", () -> new SimpleConfigurableParticleType<>(HelixConfiguration.CODEC, HelixConfiguration.STREAM, HelixConfiguration::new));
}
