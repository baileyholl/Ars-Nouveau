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

public class SelfTimeline extends BaseTimeline<SelfTimeline> {
    public static final MapCodec<SelfTimeline> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TimelineEntryData.CODEC.fieldOf("onResolvingEffect").forGetter(i -> i.onResolvingEffect),
            SoundProperty.CODEC.fieldOf("resolveSound").forGetter(i -> i.resolveSound)
    ).apply(instance, SelfTimeline::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SelfTimeline> STREAM_CODEC = StreamCodec.composite(
            TimelineEntryData.STREAM,
            SelfTimeline::onResolvingEffect,
            SoundProperty.STREAM_CODEC,
            i -> i.resolveSound,
            SelfTimeline::new);

    public static final List<IParticleMotionType<?>> RESOLVING_OPTIONS = new CopyOnWriteArrayList<>();

    public TimelineEntryData onResolvingEffect;
    public SoundProperty resolveSound = new SoundProperty();

    public SelfTimeline() {
        this(new TimelineEntryData(new BurstMotion(), new PropertyParticleOptions()), new SoundProperty());
    }

    public SelfTimeline(TimelineEntryData onResolvingEffect, SoundProperty resolveSound) {
        this.onResolvingEffect = onResolvingEffect;
        this.resolveSound = resolveSound;
    }

    public TimelineEntryData onResolvingEffect() {
        return onResolvingEffect;
    }

    @Override
    public IParticleTimelineType<SelfTimeline> getType() {
        return ParticleTimelineRegistry.SELF_TIMELINE.get();
    }

    @Override
    public List<BaseProperty<?>> getProperties() {
        return List.of(new MotionProperty(new TimelineOption(TimelineOption.IMPACT, this.onResolvingEffect, ImmutableList.copyOf(RESOLVING_OPTIONS)), List.of(resolveSound)));
    }
}
