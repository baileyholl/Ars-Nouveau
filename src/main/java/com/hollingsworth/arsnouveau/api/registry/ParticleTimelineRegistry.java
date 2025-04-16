package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.timelines.IParticleTimelineType;
import com.hollingsworth.arsnouveau.api.particle.timelines.ProjectileTimeline;
import com.hollingsworth.arsnouveau.api.particle.timelines.SimpleParticleTimelineType;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class ParticleTimelineRegistry {

    public static final ResourceKey<Registry<IParticleTimelineType<?>>> PARTICLE_TIMELINE_REGISTRY_KEY = ResourceKey.createRegistryKey(ArsNouveau.prefix("particle_timelines"));

    public static final Registry<IParticleTimelineType<?>> PARTICLE_TIMELINE_REGISTRY = new RegistryBuilder<>(PARTICLE_TIMELINE_REGISTRY_KEY).sync(true).create();
    public static final DeferredRegister<IParticleTimelineType<?>> TIMELINE_DF = DeferredRegister.create(PARTICLE_TIMELINE_REGISTRY, ArsNouveau.MODID);

    public static final DeferredHolder<IParticleTimelineType<?>, IParticleTimelineType<ProjectileTimeline>> PROJECTILE_TIMELINE = TIMELINE_DF.register("projectile", () -> new SimpleParticleTimelineType<>(ProjectileTimeline.CODEC, ProjectileTimeline.STREAM_CODEC, ProjectileTimeline::new));

    public static final MapRegistry<AbstractSpellPart, Supplier<IParticleTimelineType<?>>> PARTICLE_TIMELINE_MAP = new MapRegistry<>();

    public static void registerGlyphTimeline(AbstractSpellPart spellPart, Supplier<IParticleTimelineType<?>> timelineType) {
        PARTICLE_TIMELINE_MAP.register(spellPart, timelineType);
    }

}
