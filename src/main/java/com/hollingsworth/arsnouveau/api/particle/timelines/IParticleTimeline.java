package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public interface IParticleTimeline {
    Codec<IParticleTimeline> CODEC = IParticleTimelineType.CODEC.dispatch(IParticleTimeline::getType, IParticleTimelineType::codec);

    StreamCodec<RegistryFriendlyByteBuf, IParticleTimeline> STREAM_CODEC = ByteBufCodecs.registry(ParticleTimelineRegistry.PARTICLE_TIMELINE_REGISTRY_KEY).dispatch(IParticleTimeline::getType, IParticleTimelineType::streamCodec);

    IParticleTimelineType<?> getType();

    List<TimelineOption> getTimelineOptions();


}
