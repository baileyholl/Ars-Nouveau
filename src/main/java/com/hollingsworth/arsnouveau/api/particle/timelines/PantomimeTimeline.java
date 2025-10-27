package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.BurstMotion;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.BaseProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.MotionProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.SoundProperty;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public class PantomimeTimeline extends BaseTimeline<PantomimeTimeline> {
    public static final MapCodec<PantomimeTimeline> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TimelineEntryData.CODEC.fieldOf("onResolvingEffect").forGetter(i -> i.onResolvingEffect),
            SoundProperty.CODEC.fieldOf("resolveSound").forGetter(i -> i.resolveSound)
    ).apply(instance, PantomimeTimeline::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PantomimeTimeline> STREAM_CODEC = StreamCodec.composite(
            TimelineEntryData.STREAM,
            PantomimeTimeline::onResolvingEffect,
            SoundProperty.STREAM_CODEC,
            i -> i.resolveSound,
            PantomimeTimeline::new);


    public TimelineEntryData onResolvingEffect;
    public SoundProperty resolveSound = new SoundProperty();

    public PantomimeTimeline() {
        this(new TimelineEntryData(new BurstMotion(), new PropertyParticleOptions()), new SoundProperty());
    }

    public PantomimeTimeline(TimelineEntryData onResolvingEffect, SoundProperty resolveSound) {
        this.onResolvingEffect = onResolvingEffect;
        this.resolveSound = resolveSound;
    }

    public TimelineEntryData onResolvingEffect() {
        return onResolvingEffect;
    }

    @Override
    public IParticleTimelineType<PantomimeTimeline> getType() {
        return ParticleTimelineRegistry.PANTOMIME_TIMELINE.get();
    }

    @Override
    public List<BaseProperty<?>> getProperties() {
        return List.of(
                new MotionProperty(new TimelineOption(TimelineOption.IMPACT, this.onResolvingEffect, ImmutableList.copyOf(TouchTimeline.RESOLVING_OPTIONS)), List.of(resolveSound))
        );
    }
}
