package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.BurstMotion;
import com.hollingsworth.arsnouveau.api.particle.configurations.IParticleMotionType;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.BaseProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.MotionProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.SoundProperty;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TouchTimeline extends BaseTimeline<TouchTimeline> {
    public static final MapCodec<TouchTimeline> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TimelineEntryData.CODEC.fieldOf("onResolvingEffect").forGetter(i -> i.onResolvingEffect),
            SoundProperty.CODEC.fieldOf("resolveSound").forGetter(i -> i.resolveSound)
    ).apply(instance, TouchTimeline::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TouchTimeline> STREAM_CODEC = StreamCodec.composite(
            TimelineEntryData.STREAM,
            TouchTimeline::onResolvingEffect,
            SoundProperty.STREAM_CODEC,
            i -> i.resolveSound,
            TouchTimeline::new);

    public static final List<IParticleMotionType<?>> RESOLVING_OPTIONS = new CopyOnWriteArrayList<>();

    public TimelineEntryData onResolvingEffect;
    public SoundProperty resolveSound = new SoundProperty();

    public TouchTimeline() {
        this(new TimelineEntryData(new BurstMotion(), new PropertyParticleOptions()), new SoundProperty());
    }

    public TouchTimeline(TimelineEntryData onResolvingEffect, SoundProperty soundProperty) {
        this.onResolvingEffect = onResolvingEffect;
        this.resolveSound = soundProperty;
    }

    public TimelineEntryData onResolvingEffect() {
        return onResolvingEffect;
    }

    @Override
    public IParticleTimelineType<TouchTimeline> getType() {
        return ParticleTimelineRegistry.TOUCH_TIMELINE.get();
    }

    @Override
    public List<BaseProperty<?>> getProperties() {
        return List.of(new MotionProperty(new TimelineOption(TimelineOption.IMPACT, this.onResolvingEffect, ImmutableList.copyOf(RESOLVING_OPTIONS)), List.of(resolveSound)));
    }
}
