package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.ShockwaveMotion;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.BaseProperty;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public class EarthshakeTimeline extends BaseTimeline<EarthshakeTimeline> {

    public static final MapCodec<EarthshakeTimeline> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TimelineEntryData.CODEC.fieldOf("waveEffect").forGetter(i -> i.waveEffect)
    ).apply(instance, EarthshakeTimeline::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EarthshakeTimeline> STREAM_CODEC = StreamCodec.composite(
            TimelineEntryData.STREAM,
            EarthshakeTimeline::waveEffect,
            EarthshakeTimeline::new);

    public TimelineEntryData waveEffect;

    public EarthshakeTimeline() {
        this(new TimelineEntryData(new ShockwaveMotion(), new PropertyParticleOptions()));
    }

    public EarthshakeTimeline(TimelineEntryData waveEffect) {
        this.waveEffect = waveEffect;
    }

    public TimelineEntryData waveEffect() {
        return waveEffect;
    }

    @Override
    public IParticleTimelineType<EarthshakeTimeline> getType() {
        return ParticleTimelineRegistry.EARTHSHAKE_TIMELINE.get();
    }

    @Override
    public List<BaseProperty<?>> getProperties() {
        return waveEffect.motion().getProperties(waveEffect.particleOptions().map);
    }
}
