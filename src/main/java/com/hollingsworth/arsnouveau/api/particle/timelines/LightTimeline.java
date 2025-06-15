package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.configurations.IParticleMotionType;
import com.hollingsworth.arsnouveau.api.particle.configurations.LightBlobMotion;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.BaseProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.MotionProperty;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LightTimeline extends BaseTimeline<LightTimeline>{
    public static final MapCodec<LightTimeline> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TimelineEntryData.CODEC.fieldOf("onResolvingEffect").forGetter(i -> i.onTickEffect)
    ).apply(instance, LightTimeline::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LightTimeline> STREAM_CODEC = StreamCodec.composite(
            TimelineEntryData.STREAM,
            LightTimeline::tickingEffect,
            LightTimeline::new);

    public static final List<IParticleMotionType<?>> TICKING_OPTIONS = new CopyOnWriteArrayList<>();

    public TimelineEntryData onTickEffect;

    public LightTimeline(){
        this(new TimelineEntryData(new LightBlobMotion()));
    }

    public LightTimeline(TimelineEntryData onTickEffect){
        this.onTickEffect = onTickEffect;
    }

    public TimelineEntryData tickingEffect(){
        return onTickEffect;
    }

    @Override
    public IParticleTimelineType<LightTimeline> getType() {
        return ParticleTimelineRegistry.LIGHT_TIMELINE.get();
    }

    @Override
    public List<BaseProperty<?>> getProperties() {
        return List.of(
                new MotionProperty(new TimelineOption(ArsNouveau.prefix("tick"), this.onTickEffect, ImmutableList.copyOf(TICKING_OPTIONS)))
        );
    }
}
