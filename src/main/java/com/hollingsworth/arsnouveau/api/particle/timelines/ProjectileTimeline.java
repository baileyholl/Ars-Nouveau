package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.BurstMotion;
import com.hollingsworth.arsnouveau.api.particle.configurations.IParticleMotionType;
import com.hollingsworth.arsnouveau.api.particle.configurations.TrailMotion;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ParticleTypeProperty;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProjectileTimeline implements IParticleTimeline{
    public static final MapCodec<ProjectileTimeline> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TimelineEntryData.CODEC.fieldOf("trailEffect").forGetter(i -> i.trailEffect),
            TimelineEntryData.CODEC.fieldOf("onResolvingEffect").forGetter(i -> i.onResolvingEffect)
    ).apply(instance, ProjectileTimeline::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ProjectileTimeline> STREAM_CODEC = StreamCodec.composite(TimelineEntryData.STREAM, ProjectileTimeline::trailEffect,
            TimelineEntryData.STREAM,
            ProjectileTimeline::onResolvingEffect,
            ProjectileTimeline::new);

    public static final List<IParticleMotionType<?>> TRAIL_OPTIONS = new CopyOnWriteArrayList<>();
    public static final List<IParticleMotionType<?>> RESOLVING_OPTIONS = new CopyOnWriteArrayList<>();

    public TimelineEntryData trailEffect;
    public TimelineEntryData onResolvingEffect;

    public ProjectileTimeline(){
        this(new TimelineEntryData(new TrailMotion(), PropertyParticleOptions.defaultGlow()),
                new TimelineEntryData(new BurstMotion(), PropertyParticleOptions.defaultGlow()));
    }

    public ProjectileTimeline(TimelineEntryData trailEffect, TimelineEntryData onResolvingEffect){
        this.trailEffect = trailEffect;
        this.onResolvingEffect = onResolvingEffect;
    }

    public TimelineEntryData trailEffect(){
        return trailEffect;
    }

    public TimelineEntryData onResolvingEffect(){
        return onResolvingEffect;
    }

    @Override
    public IParticleTimelineType<?> getType() {
        return ParticleTimelineRegistry.PROJECTILE_TIMELINE.get();
    }

    @Override
    public List<TimelineOption> getTimelineOptions() {
        return List.of(new TimelineOption(ArsNouveau.prefix("trail"), this.trailEffect, ImmutableList.copyOf(TRAIL_OPTIONS)).withProperty(new ParticleTypeProperty(this.trailEffect.particleOptions.map)),
                new TimelineOption(ArsNouveau.prefix("impact"), this.onResolvingEffect, ImmutableList.copyOf(RESOLVING_OPTIONS)).withProperty(new ParticleTypeProperty(this.onResolvingEffect.particleOptions.map)));
    }
}
