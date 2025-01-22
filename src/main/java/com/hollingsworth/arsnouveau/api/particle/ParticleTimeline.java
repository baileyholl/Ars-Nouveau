package com.hollingsworth.arsnouveau.api.particle;

import com.hollingsworth.arsnouveau.api.particle.configurations.BurstConfiguration;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleTrail;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class ParticleTimeline {

    public static final MapCodec<ParticleTimeline> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            IParticleCallback.CODEC.fieldOf("trailEffect").forGetter(i -> i.trailEffect),
            IParticleCallback.CODEC.fieldOf("onResolvingEffect").forGetter(i -> i.onResolvingEffect)
    ).apply(instance, ParticleTimeline::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleTimeline> STREAM_CODEC = StreamCodec.composite(IParticleCallback.STREAM_CODEC, ParticleTimeline::trailEffect,
            IParticleCallback.STREAM_CODEC,
            ParticleTimeline::onResolvingEffect,
            ParticleTimeline::new);


    public static ParticleTimeline defaultTimeline(){
        return new ParticleTimeline(new ParticleTrail(GlowParticleData.createData(ParticleColor.defaultParticleColor())), new BurstConfiguration(GlowParticleData.createData(ParticleColor.defaultParticleColor())));
    }

    public IParticleCallback trailEffect;
    public IParticleCallback onResolvingEffect;

    public ParticleTimeline(IParticleCallback trailEffect, IParticleCallback onResolvingEffect){
        this.trailEffect = trailEffect;
        this.onResolvingEffect = onResolvingEffect;
    }

    public IParticleCallback trailEffect(){
        return trailEffect;
    }

    public IParticleCallback onResolvingEffect(){
        return onResolvingEffect;
    }
}
