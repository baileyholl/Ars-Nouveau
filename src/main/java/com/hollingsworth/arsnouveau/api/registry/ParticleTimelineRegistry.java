package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.timelines.*;
import com.hollingsworth.arsnouveau.common.spell.effect.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodPantomime;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ParticleTimelineRegistry {

    public static final ResourceKey<Registry<IParticleTimelineType<?>>> PARTICLE_TIMELINE_REGISTRY_KEY = ResourceKey.createRegistryKey(ArsNouveau.prefix("particle_timelines"));

    public static final Registry<IParticleTimelineType<? extends IParticleTimeline<?>>> PARTICLE_TIMELINE_REGISTRY = new RegistryBuilder<>(PARTICLE_TIMELINE_REGISTRY_KEY).sync(true).create();
    public static final DeferredRegister<IParticleTimelineType<?>> TIMELINE_DF = DeferredRegister.create(PARTICLE_TIMELINE_REGISTRY, ArsNouveau.MODID);

    public static final DeferredHolder<IParticleTimelineType<?>, IParticleTimelineType<ProjectileTimeline>> PROJECTILE_TIMELINE = TIMELINE_DF.register("projectile", () -> new SimpleParticleTimelineType<>(MethodProjectile.INSTANCE, ProjectileTimeline.CODEC, ProjectileTimeline.STREAM_CODEC, ProjectileTimeline::new));

    public static final DeferredHolder<IParticleTimelineType<?>, IParticleTimelineType<OrbitTimeline>> ORBIT_TIMELINE = TIMELINE_DF.register("orbit", () -> new SimpleParticleTimelineType<>(EffectOrbit.INSTANCE, OrbitTimeline.CODEC, OrbitTimeline.STREAM_CODEC, OrbitTimeline::new));

    public static final DeferredHolder<IParticleTimelineType<?>, IParticleTimelineType<WallTimeline>> WALL_TIMELINE = TIMELINE_DF.register("wall", () -> new SimpleParticleTimelineType<>(EffectWall.INSTANCE, WallTimeline.CODEC, WallTimeline.STREAM_CODEC, WallTimeline::new));

    public static final DeferredHolder<IParticleTimelineType<?>, IParticleTimelineType<LingerTimeline>> LINGER_TIMELINE = TIMELINE_DF.register("linger", () -> new SimpleParticleTimelineType<>(EffectLinger.INSTANCE, LingerTimeline.CODEC, LingerTimeline.STREAM_CODEC, LingerTimeline::new));

    public static final DeferredHolder<IParticleTimelineType<?>, IParticleTimelineType<TouchTimeline>> TOUCH_TIMELINE = TIMELINE_DF.register("touch", () -> new SimpleParticleTimelineType<>(MethodTouch.INSTANCE, TouchTimeline.CODEC, TouchTimeline.STREAM_CODEC, TouchTimeline::new));

    public static final DeferredHolder<IParticleTimelineType<?>, IParticleTimelineType<BurstTimeline>> BURST_TIMELINE = TIMELINE_DF.register("burst", () -> new SimpleParticleTimelineType<>(EffectBurst.INSTANCE, BurstTimeline.CODEC, BurstTimeline.STREAM_CODEC, BurstTimeline::new));

    public static final DeferredHolder<IParticleTimelineType<?>, IParticleTimelineType<LightTimeline>> LIGHT_TIMELINE = TIMELINE_DF.register("light", () -> new SimpleParticleTimelineType<>(EffectLight.INSTANCE, LightTimeline.CODEC, LightTimeline.STREAM_CODEC, LightTimeline::new));


    public static final DeferredHolder<IParticleTimelineType<?>, IParticleTimelineType<RuneTimeline>> RUNE_TIMELINE = TIMELINE_DF.register("rune", () -> new SimpleParticleTimelineType<>(EffectRune.INSTANCE, RuneTimeline.CODEC, RuneTimeline.STREAM_CODEC, RuneTimeline::new));



    public static final DeferredHolder<IParticleTimelineType<?>, IParticleTimelineType<PantomimeTimeline>> PANTOMIME_TIMELINE = TIMELINE_DF.register("pantomime", () -> new SimpleParticleTimelineType<>(MethodPantomime.INSTANCE, PantomimeTimeline.CODEC, PantomimeTimeline.STREAM_CODEC, PantomimeTimeline::new));


}
