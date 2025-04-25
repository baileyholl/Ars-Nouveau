package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.*;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ParticlePropertyRegistry {
    public static final ResourceKey<Registry<IPropertyType<?>>> PARTICLE_PROPERTY_KEY = ResourceKey.createRegistryKey(ArsNouveau.prefix("particle_properties"));

    public static final Registry<IPropertyType<?>> PARTICLE_PROPERTY_REGISTRY = new RegistryBuilder<>(PARTICLE_PROPERTY_KEY).sync(true).create();

    public static final Codec<IPropertyType<?>> CODEC = Codec.lazyInitialized(PARTICLE_PROPERTY_REGISTRY::byNameCodec);

    public static final DeferredRegister<IPropertyType<?>> PROP_DF = DeferredRegister.create(PARTICLE_PROPERTY_REGISTRY, ArsNouveau.MODID);

    public static final DeferredHolder<IPropertyType<?>, IPropertyType<ColorProperty>> COLOR_PROPERTY = PROP_DF.register("color", () -> new SimplePropertyType<>(ColorProperty.CODEC, ColorProperty.STREAM_CODEC));
    public static final DeferredHolder<IPropertyType<?>, IPropertyType<ParticleTypeProperty>> TYPE_PROPERTY = PROP_DF.register("particle_type", () -> new SimplePropertyType<>(ParticleTypeProperty.CODEC, ParticleTypeProperty.STREAM_CODEC));

    public static final DeferredHolder<IPropertyType<?>, IPropertyType<ParticleDensityProperty>> DENSITY_PROPERTY = PROP_DF.register("density", () -> new SimplePropertyType<>(ParticleDensityProperty.CODEC, ParticleDensityProperty.STREAM_CODEC));

    public static final DeferredHolder<IPropertyType<?>, IPropertyType<EmitterProperty>> EMITTER_PROPERTY = PROP_DF.register("emitter", () -> new SimplePropertyType<>(EmitterProperty.CODEC, EmitterProperty.STREAM_CODEC));
}
