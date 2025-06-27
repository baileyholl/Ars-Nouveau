package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.api.particle.configurations.IParticleMotionType;
import com.hollingsworth.arsnouveau.api.particle.configurations.LightBlobMotion;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.BaseProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.MotionProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.SoundProperty;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PrestidigitationTimeline extends BaseTimeline<PrestidigitationTimeline> {
    public static final MapCodec<PrestidigitationTimeline> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TimelineEntryData.CODEC.fieldOf("onResolvingEffect").forGetter(i -> i.onTickEffect),
            SoundProperty.CODEC.fieldOf("randomSound").forGetter(i -> i.randomSound)
    ).apply(instance, PrestidigitationTimeline::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PrestidigitationTimeline> STREAM_CODEC = StreamCodec.composite(
            TimelineEntryData.STREAM,
            PrestidigitationTimeline::tickingEffect,
            SoundProperty.STREAM_CODEC,
            PrestidigitationTimeline::randomSound,
            PrestidigitationTimeline::new);

    public static final List<IParticleMotionType<?>> TICKING_OPTIONS = new CopyOnWriteArrayList<>();

    public TimelineEntryData onTickEffect;
    public SoundProperty randomSound = new SoundProperty();

    public PrestidigitationTimeline() {
        this(new TimelineEntryData(new LightBlobMotion()), new SoundProperty(ConfiguredSpellSound.EMPTY));
    }

    public PrestidigitationTimeline(TimelineEntryData onTickEffect, SoundProperty randomSound) {
        this.onTickEffect = onTickEffect;
        this.randomSound = randomSound;
    }

    public TimelineEntryData tickingEffect() {
        return onTickEffect;
    }

    public SoundProperty randomSound() {
        return randomSound;
    }

    @Override
    public IParticleTimelineType<PrestidigitationTimeline> getType() {
        return ParticleTimelineRegistry.PRESTIDIGITATION_TIMELINE.get();
    }

    @Override
    public List<BaseProperty<?>> getProperties() {
        return List.of(
                new MotionProperty(new TimelineOption(TimelineOption.TICK, this.onTickEffect, ImmutableList.copyOf(TICKING_OPTIONS)), List.of(randomSound))
        );
    }
}
