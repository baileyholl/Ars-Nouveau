package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.BurstMotion;
import com.hollingsworth.arsnouveau.api.particle.configurations.IParticleMotionType;
import com.hollingsworth.arsnouveau.api.particle.configurations.TrailMotion;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.BaseProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ModelProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.MotionProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.SoundProperty;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProjectileTimeline extends BaseTimeline<ProjectileTimeline> {
    public static final MapCodec<ProjectileTimeline> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TimelineEntryData.CODEC.fieldOf("trailEffect").forGetter(i -> i.trailEffect),
            TimelineEntryData.CODEC.fieldOf("onResolvingEffect").forGetter(i -> i.onResolvingEffect),
            TimelineEntryData.CODEC.fieldOf("onSpawnEffect").forGetter(i -> i.onSpawnEffect),
            TimelineEntryData.CODEC.fieldOf("flairEffect").forGetter(i -> i.flairEffect),
            SoundProperty.CODEC.fieldOf("castSound").forGetter(i -> i.castSound),
            SoundProperty.CODEC.fieldOf("resolveSound").forGetter(i -> i.resolveSound)
    ).apply(instance, ProjectileTimeline::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ProjectileTimeline> STREAM_CODEC = StreamCodec.composite(TimelineEntryData.STREAM, ProjectileTimeline::trailEffect,
            TimelineEntryData.STREAM,
            ProjectileTimeline::onResolvingEffect,
            TimelineEntryData.STREAM,
            ProjectileTimeline::onSpawnEffect,
            TimelineEntryData.STREAM,
            ProjectileTimeline::flairEffect,
            SoundProperty.STREAM_CODEC,
            i -> i.castSound,
            SoundProperty.STREAM_CODEC,
            i -> i.resolveSound,
            ProjectileTimeline::new);

    public static final List<IParticleMotionType<?>> TRAIL_OPTIONS = new CopyOnWriteArrayList<>();
    public static final List<IParticleMotionType<?>> RESOLVING_OPTIONS = new CopyOnWriteArrayList<>();
    public static final List<IParticleMotionType<?>> SPAWN_OPTIONS = new CopyOnWriteArrayList<>();
    public static final List<IParticleMotionType<?>> FLAIR_OPTIONS = new CopyOnWriteArrayList<>();

    public TimelineEntryData onSpawnEffect;
    public TimelineEntryData trailEffect;
    public TimelineEntryData onResolvingEffect;
    public TimelineEntryData flairEffect;

    public SoundProperty castSound = new SoundProperty();
    public SoundProperty resolveSound = new SoundProperty();

    public ProjectileTimeline() {
        this(new TimelineEntryData(new TrailMotion(), new PropertyParticleOptions()),
                new TimelineEntryData(new BurstMotion(), new PropertyParticleOptions()),
                new TimelineEntryData(),
                new TimelineEntryData(),
                new SoundProperty(),
                new SoundProperty());
    }

    public ProjectileTimeline(TimelineEntryData trailEffect, TimelineEntryData onResolvingEffect,
                              TimelineEntryData onSpawnEffect, TimelineEntryData flairEffect, SoundProperty castSound, SoundProperty resolveSound) {
        this.trailEffect = trailEffect;
        this.onResolvingEffect = onResolvingEffect;
        this.onSpawnEffect = onSpawnEffect;
        this.flairEffect = flairEffect;
        this.castSound = castSound;
        this.resolveSound = resolveSound;
    }

    public TimelineEntryData trailEffect() {
        return trailEffect;
    }

    public TimelineEntryData onResolvingEffect() {
        return onResolvingEffect;
    }

    public TimelineEntryData onSpawnEffect() {
        return onSpawnEffect;
    }

    public TimelineEntryData flairEffect() {
        return flairEffect;
    }

    @Override
    public IParticleTimelineType<ProjectileTimeline> getType() {
        return ParticleTimelineRegistry.PROJECTILE_TIMELINE.get();
    }

    @Override
    public List<BaseProperty<?>> getProperties() {
        return List.of(this.trailEffect.motion().propertyMap.createIfMissing(new ModelProperty()),
                new MotionProperty(new TimelineOption(TimelineOption.SPAWN, onSpawnEffect, ImmutableList.copyOf(SPAWN_OPTIONS)), List.of(castSound)),
                new MotionProperty(new TimelineOption(TimelineOption.TRAIL, trailEffect, ImmutableList.copyOf(TRAIL_OPTIONS))),
                new MotionProperty(new TimelineOption(TimelineOption.IMPACT, onResolvingEffect, ImmutableList.copyOf(RESOLVING_OPTIONS)), List.of(resolveSound)),
                new MotionProperty(new TimelineOption(TimelineOption.FLAIR, flairEffect, ImmutableList.copyOf(FLAIR_OPTIONS))));
    }
}
