package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import com.hollingsworth.arsnouveau.api.particle.timelines.PrestidigitationTimeline;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineEntryData;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

import java.util.Objects;

public class PrestidigitationData {
    public static Codec<PrestidigitationData> CODEC = PrestidigitationTimeline.CODEC.xmap(PrestidigitationData::new, PrestidigitationData::timeline).codec();
    public static StreamCodec<RegistryFriendlyByteBuf, PrestidigitationData> STREAM = StreamCodec.composite(
            PrestidigitationTimeline.STREAM_CODEC,
            PrestidigitationData::timeline,
            PrestidigitationData::new
    );

    public PrestidigitationData(PrestidigitationTimeline timeline) {
        this.timeline = timeline;
    }

    PrestidigitationTimeline timeline;
    ParticleEmitter emitter;

    public ParticleEmitter getEmitter(Entity entity, TimelineEntryData data) {
        if (emitter == null) {
            this.emitter = new ParticleEmitter(entity, data);
        }
        return emitter;
    }

    public PrestidigitationTimeline timeline() {
        return timeline;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PrestidigitationData that = (PrestidigitationData) o;
        return Objects.equals(timeline, that.timeline);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(timeline);
    }
}
