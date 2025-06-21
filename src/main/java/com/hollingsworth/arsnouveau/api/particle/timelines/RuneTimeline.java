package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.hollingsworth.arsnouveau.api.particle.configurations.properties.BaseProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ColorProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.PropMap;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.RuneTextureProperty;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public class RuneTimeline extends BaseTimeline<RuneTimeline> {
    public static final MapCodec<RuneTimeline> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PropMap.CODEC.fieldOf("propMap").forGetter(i -> i.propMap)
    ).apply(instance, RuneTimeline::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RuneTimeline> STREAM_CODEC = StreamCodec.composite(
            PropMap.STREAM_CODEC,
            i -> i.propMap,
            RuneTimeline::new);


    public PropMap propMap;

    public RuneTimeline() {
        this(new PropMap());
    }

    public RuneTimeline(PropMap propMap) {
        this.propMap = propMap;
    }

    public ParticleColor getColor() {
        return propMap.getOrDefault(ParticlePropertyRegistry.COLOR_PROPERTY.get(), new ColorProperty()).particleColor;
    }

    public String getTexture() {
        return propMap.getOrDefault(ParticlePropertyRegistry.RUNE_PROPERTY.get(), new RuneTextureProperty()).runeTexture;
    }

    @Override
    public IParticleTimelineType<RuneTimeline> getType() {
        return ParticleTimelineRegistry.RUNE_TIMELINE.get();
    }

    @Override
    public List<BaseProperty<?>> getProperties() {
        return List.of(propMap.createIfMissing(new ColorProperty()), propMap.createIfMissing(new RuneTextureProperty()));
    }
}
