package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.api.particle.configurations.BurstMotion;
import com.hollingsworth.arsnouveau.api.particle.configurations.IParticleMotionType;
import com.hollingsworth.arsnouveau.api.particle.configurations.LightBlobMotion;
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

public class DelayTimeline extends BaseTimeline<DelayTimeline> {
    public static final MapCodec<DelayTimeline> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TimelineEntryData.CODEC.fieldOf("onTickEffect").forGetter(i -> i.onTickEffect),
            TimelineEntryData.CODEC.fieldOf("resolvingEffect").forGetter(i -> i.onResolvingEffect),
            SoundProperty.CODEC.fieldOf("resolvingSound").forGetter(i -> i.resolvingSound)
    ).apply(instance, DelayTimeline::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DelayTimeline> STREAM_CODEC = StreamCodec.composite(
            TimelineEntryData.STREAM,
            DelayTimeline::tickingEffect,
            TimelineEntryData.STREAM,
            DelayTimeline::onResolvingEffect,
            SoundProperty.STREAM_CODEC,
            DelayTimeline::resolvingSound,
            DelayTimeline::new);

    public static final List<IParticleMotionType<?>> TICKING_OPTIONS = new CopyOnWriteArrayList<>();
    public static final List<IParticleMotionType<?>> RESOLVING_OPTIONS = new CopyOnWriteArrayList<>();

    public TimelineEntryData onTickEffect;
    public TimelineEntryData onResolvingEffect;
    public SoundProperty resolvingSound;

    public DelayTimeline() {
        this(new TimelineEntryData(new LightBlobMotion()), new TimelineEntryData(new BurstMotion()), new SoundProperty());
    }

    public DelayTimeline(TimelineEntryData onTickEffect, TimelineEntryData onResolvingEffect, SoundProperty resolvingSound) {
        this.onTickEffect = onTickEffect;
        this.onResolvingEffect = onResolvingEffect;
        this.resolvingSound = resolvingSound;
    }

    public TimelineEntryData tickingEffect() {
        return onTickEffect;
    }

    public TimelineEntryData onResolvingEffect() {
        return onResolvingEffect;
    }

    public SoundProperty resolvingSound() {
        return resolvingSound;
    }

    @Override
    public IParticleTimelineType<DelayTimeline> getType() {
        return ParticleTimelineRegistry.DELAY_TIMELINE.get();
    }

    @Override
    public List<BaseProperty<?>> getProperties() {
        return List.of(
                new MotionProperty(new TimelineOption(TimelineOption.TICK, this.onTickEffect, ImmutableList.copyOf(TICKING_OPTIONS))),
                new MotionProperty(new TimelineOption(TimelineOption.IMPACT, this.onResolvingEffect, ImmutableList.copyOf(RESOLVING_OPTIONS)), List.of(resolvingSound))
        );
    }
}
