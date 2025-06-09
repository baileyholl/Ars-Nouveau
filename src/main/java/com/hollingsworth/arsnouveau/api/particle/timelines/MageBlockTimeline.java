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

public class MageBlockTimeline extends BaseTimeline<MageBlockTimeline>{
    public static final MapCodec<MageBlockTimeline> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PropMap.CODEC.fieldOf("propMap").forGetter(i -> i.propMap)
    ).apply(instance, MageBlockTimeline::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MageBlockTimeline> STREAM_CODEC = StreamCodec.composite(
            PropMap.STREAM_CODEC,
            i -> i.propMap,
            MageBlockTimeline::new);


    public PropMap propMap;

    public MageBlockTimeline(){
        this(new PropMap());
    }

    public MageBlockTimeline(PropMap propMap){
        this.propMap = propMap;
    }

    public ParticleColor getColor(){
        return propMap.getOrDefault(ParticlePropertyRegistry.COLOR_PROPERTY.get(), new ColorProperty(ParticleColor.defaultParticleColor(), true)).particleColor;
    }

    @Override
    public IParticleTimelineType<MageBlockTimeline> getType() {
        return ParticleTimelineRegistry.MAGEBLOCK_TIMELINE.get();
    }

    @Override
    public List<BaseProperty<?>> getProperties() {
        return List.of(propMap.createIfMissing(new ColorProperty()));
    }
}
