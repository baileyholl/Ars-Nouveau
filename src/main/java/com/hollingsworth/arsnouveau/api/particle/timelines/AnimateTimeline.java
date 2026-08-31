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

public class AnimateTimeline extends BaseTimeline<AnimateTimeline> {
    public static final MapCodec<AnimateTimeline> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PropMap.CODEC.fieldOf("propMap").forGetter(i -> i.propMap)
    ).apply(instance, AnimateTimeline::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AnimateTimeline> STREAM_CODEC = StreamCodec.composite(
            PropMap.STREAM_CODEC,
            i -> i.propMap,
            AnimateTimeline::new);


    public PropMap propMap;

    public AnimateTimeline() {
        this(new PropMap());
    }

    public AnimateTimeline(PropMap propMap) {
        this.propMap = propMap;
    }

    public ParticleColor getColor() {
        return propMap.getOrDefault(ParticlePropertyRegistry.COLOR_PROPERTY.get(), new ColorProperty()).particleColor;
    }

    @Override
    public IParticleTimelineType<AnimateTimeline> getType() {
        return ParticleTimelineRegistry.ANIMATE_TIMELINE.get();
    }

    @Override
    public List<BaseProperty<?>> getProperties() {
        return List.of(propMap.createIfMissing(new ColorProperty()));
    }
}