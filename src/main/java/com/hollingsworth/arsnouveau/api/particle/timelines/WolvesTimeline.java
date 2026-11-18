package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.hollingsworth.arsnouveau.api.particle.configurations.properties.BaseProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ColorProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.PropMap;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public class WolvesTimeline extends BaseTimeline<WolvesTimeline> {
    public static final MapCodec<WolvesTimeline> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PropMap.CODEC.fieldOf("propMap").forGetter(i -> i.propMap)
    ).apply(instance, WolvesTimeline::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WolvesTimeline> STREAM_CODEC = StreamCodec.composite(
            PropMap.STREAM_CODEC,
            i -> i.propMap,
            WolvesTimeline::new);


    public PropMap propMap;

    public WolvesTimeline() {
        this(new PropMap());
    }

    public WolvesTimeline(PropMap propMap) {
        this.propMap = propMap;
    }

    public ParticleColor getColor() {
        return propMap.getOrDefault(ParticlePropertyRegistry.COLOR_PROPERTY.get(), new ColorProperty()).particleColor;
    }

    @Override
    public IParticleTimelineType<WolvesTimeline> getType() {
        return ParticleTimelineRegistry.WOLVES_TIMELINE.get();
    }

    @Override
    public List<BaseProperty<?>> getProperties() {
        return List.of(propMap.createIfMissing(new ColorProperty()));
    }
}