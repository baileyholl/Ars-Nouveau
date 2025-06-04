package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.BurstMotion;
import com.hollingsworth.arsnouveau.api.particle.configurations.IParticleMotionType;
import com.hollingsworth.arsnouveau.api.particle.configurations.TrailMotion;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.BaseProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.MotionProperty;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.hollingsworth.arsnouveau.api.particle.timelines.ProjectileTimeline.FLAIR_OPTIONS;
import static com.hollingsworth.arsnouveau.api.particle.timelines.ProjectileTimeline.SPAWN_OPTIONS;

public class OrbitTimeline extends BaseTimeline<OrbitTimeline>{
    public static final MapCodec<OrbitTimeline> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TimelineEntryData.CODEC.fieldOf("trailEffect").forGetter(i -> i.trailEffect),
            TimelineEntryData.CODEC.fieldOf("onResolvingEffect").forGetter(i -> i.onResolvingEffect),
            TimelineEntryData.CODEC.fieldOf("onSpawnEffect").forGetter(i -> i.onSpawnEffect),
            TimelineEntryData.CODEC.fieldOf("flairEffect").forGetter(i -> i.flairEffect)
    ).apply(instance, OrbitTimeline::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, OrbitTimeline> STREAM_CODEC = StreamCodec.composite(TimelineEntryData.STREAM, OrbitTimeline::trailEffect,
            TimelineEntryData.STREAM,
            OrbitTimeline::onResolvingEffect,
            TimelineEntryData.STREAM,
            OrbitTimeline::onSpawnEffect,
            TimelineEntryData.STREAM,
            OrbitTimeline::flairEffect,
            OrbitTimeline::new);

    public static final List<IParticleMotionType<?>> TRAIL_OPTIONS = new CopyOnWriteArrayList<>();
    public static final List<IParticleMotionType<?>> RESOLVING_OPTIONS = new CopyOnWriteArrayList<>();

    public TimelineEntryData trailEffect;
    public TimelineEntryData onResolvingEffect;
    public TimelineEntryData onSpawnEffect;
    public TimelineEntryData flairEffect;
    public OrbitTimeline(){
        this(new TimelineEntryData(new TrailMotion(), PropertyParticleOptions.defaultGlow()),
                new TimelineEntryData(new BurstMotion(), PropertyParticleOptions.defaultGlow()),
                new TimelineEntryData(),
                new TimelineEntryData());
    }

    public OrbitTimeline(TimelineEntryData trailEffect, TimelineEntryData onResolvingEffect,
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
    public IParticleTimelineType<OrbitTimeline> getType() {
        return ParticleTimelineRegistry.ORBIT_TIMELINE.get();
    }

    @Override
    public List<BaseProperty<?>> getProperties() {
        return List.of(
                new MotionProperty(new TimelineOption(ArsNouveau.prefix("trail"), this.trailEffect, ImmutableList.copyOf(TRAIL_OPTIONS))),
                new MotionProperty(new TimelineOption(ArsNouveau.prefix("impact"), this.onResolvingEffect, ImmutableList.copyOf(RESOLVING_OPTIONS))),
                new MotionProperty(new TimelineOption(ArsNouveau.prefix("spawn"), this.onSpawnEffect, ImmutableList.copyOf(SPAWN_OPTIONS))),
                new MotionProperty(new TimelineOption(ArsNouveau.prefix("flair"), this.flairEffect, ImmutableList.copyOf(FLAIR_OPTIONS)))
        );
    }
}
