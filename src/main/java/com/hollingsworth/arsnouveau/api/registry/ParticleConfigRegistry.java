package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.configurations.BurstConfiguration;
import com.hollingsworth.arsnouveau.api.particle.configurations.IParticleType;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleTrail;
import com.hollingsworth.arsnouveau.api.particle.configurations.SimpleParticleType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ParticleConfigRegistry {
    public static final ResourceKey<Registry<IParticleType<?>>> PARTICLE_CONFIG_REGISTRY_KEY = ResourceKey.createRegistryKey(ArsNouveau.prefix("particle_configs"));

    public static final Registry<IParticleType<?>> PARTICLE_CONFIG_REGISTRY = new RegistryBuilder<>(PARTICLE_CONFIG_REGISTRY_KEY).sync(true).create();
    public static final DeferredRegister<IParticleType<?>> PARTICLE_CONFIG = DeferredRegister.create(PARTICLE_CONFIG_REGISTRY, ArsNouveau.MODID);

    public static final DeferredHolder<IParticleType<?>, IParticleType<BurstConfiguration>> BURST_TYPE = PARTICLE_CONFIG.register("burst", () -> new SimpleParticleType<>(BurstConfiguration.CODEC, BurstConfiguration.STREAM));

    public static final DeferredHolder<IParticleType<?>, IParticleType<ParticleTrail>> TRAIL_TYPE = PARTICLE_CONFIG.register("trail", () -> new SimpleParticleType<>(ParticleTrail.CODEC, ParticleTrail.STREAM));
}
