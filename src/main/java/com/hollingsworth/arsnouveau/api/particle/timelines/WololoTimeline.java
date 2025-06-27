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

public class WololoTimeline extends BaseTimeline<WololoTimeline> {
    public static final MapCodec<WololoTimeline> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PropMap.CODEC.fieldOf("propMap").forGetter(i -> i.propMap)
    ).apply(instance, WololoTimeline::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WololoTimeline> STREAM_CODEC = StreamCodec.composite(
            PropMap.STREAM_CODEC,
            i -> i.propMap,
            WololoTimeline::new);


    public PropMap propMap;

    public WololoTimeline() {
        this(new PropMap());
    }

    public WololoTimeline(PropMap propMap) {
        this.propMap = propMap;
    }

    public ParticleColor getColor() {
        return propMap.getOrDefault(ParticlePropertyRegistry.COLOR_PROPERTY.get(), new ColorProperty()).particleColor;
    }

    @Override
    public IParticleTimelineType<WololoTimeline> getType() {
        return ParticleTimelineRegistry.WOLOLO_TIMELINE.get();
    }

    @Override
    public List<BaseProperty<?>> getProperties() {
        return List.of(propMap.createIfMissing(new ColorProperty()));
    }
}
