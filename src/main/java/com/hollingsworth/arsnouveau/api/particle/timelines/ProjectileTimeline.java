package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.BurstMotion;
import com.hollingsworth.arsnouveau.api.particle.configurations.IParticleMotionType;
import com.hollingsworth.arsnouveau.api.particle.configurations.TrailMotion;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ModelProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ParticleTypeProperty;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProjectileTimeline extends BaseTimeline<ProjectileTimeline>{
    public static final MapCodec<ProjectileTimeline> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TimelineEntryData.CODEC.fieldOf("trailEffect").forGetter(i -> i.trailEffect),
            TimelineEntryData.CODEC.fieldOf("onResolvingEffect").forGetter(i -> i.onResolvingEffect),
            TimelineEntryData.CODEC.fieldOf("onSpawnEffect").forGetter(i -> i.onSpawnEffect),
            TimelineEntryData.CODEC.fieldOf("flairEffect").forGetter(i -> i.flairEffect)
    ).apply(instance, ProjectileTimeline::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ProjectileTimeline> STREAM_CODEC = StreamCodec.composite(TimelineEntryData.STREAM, ProjectileTimeline::trailEffect,
            TimelineEntryData.STREAM,
            ProjectileTimeline::onResolvingEffect,
            TimelineEntryData.STREAM,
            ProjectileTimeline::onSpawnEffect,
            TimelineEntryData.STREAM,
            ProjectileTimeline::flairEffect,
            ProjectileTimeline::new);

    public static final List<IParticleMotionType<?>> TRAIL_OPTIONS = new CopyOnWriteArrayList<>();
    public static final List<IParticleMotionType<?>> RESOLVING_OPTIONS = new CopyOnWriteArrayList<>();
    public static final List<IParticleMotionType<?>> SPAWN_OPTIONS = new CopyOnWriteArrayList<>();
    public static final List<IParticleMotionType<?>> FLAIR_OPTIONS = new CopyOnWriteArrayList<>();

    public TimelineEntryData onSpawnEffect;
    public TimelineEntryData trailEffect;
    public TimelineEntryData onResolvingEffect;
    public TimelineEntryData flairEffect;

    public ProjectileTimeline(){
        this(new TimelineEntryData(new TrailMotion(), PropertyParticleOptions.defaultGlow()),
                new TimelineEntryData(new BurstMotion(), PropertyParticleOptions.defaultGlow()),
                new TimelineEntryData(),
                new TimelineEntryData());
    }

    public ProjectileTimeline(TimelineEntryData trailEffect, TimelineEntryData onResolvingEffect,
                              TimelineEntryData onSpawnEffect, TimelineEntryData flairEffect){
        this.trailEffect = trailEffect;
        this.onResolvingEffect = onResolvingEffect;
        this.onSpawnEffect = onSpawnEffect;
        this.flairEffect = flairEffect;
    }

    public TimelineEntryData trailEffect(){
        return trailEffect;
    }

    public TimelineEntryData onResolvingEffect(){
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
    public List<TimelineOption> getTimelineOptions() {
        return List.of(new TimelineOption(ArsNouveau.prefix("trail"), this.trailEffect, ImmutableList.copyOf(TRAIL_OPTIONS))
                        .withProperty(new ModelProperty(this.trailEffect.particleOptions.map))
                        .withProperty(new ParticleTypeProperty(this.trailEffect.particleOptions.map)),
                new TimelineOption(ArsNouveau.prefix("impact"), this.onResolvingEffect, ImmutableList.copyOf(RESOLVING_OPTIONS))
                        .withProperty(new ParticleTypeProperty(this.onResolvingEffect.particleOptions.map)),
                new TimelineOption(ArsNouveau.prefix("spawn"), this.onSpawnEffect, ImmutableList.copyOf(SPAWN_OPTIONS))
                        .withProperty(new ParticleTypeProperty(this.onSpawnEffect.particleOptions.map)),
                new TimelineOption(ArsNouveau.prefix("flair"), this.flairEffect, ImmutableList.copyOf(FLAIR_OPTIONS))
                        .withProperty(new ParticleTypeProperty(this.flairEffect.particleOptions.map)));
    }
}
