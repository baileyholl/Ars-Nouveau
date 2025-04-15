package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.hollingsworth.arsnouveau.api.particle.configurations.BurstConfiguration;
import com.hollingsworth.arsnouveau.api.particle.configurations.IParticleConfig;
import com.hollingsworth.arsnouveau.api.particle.configurations.TrailConfiguration;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class ParticleTimeline {

    public static final MapCodec<ParticleTimeline> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            IParticleConfig.CODEC.fieldOf("trailEffect").forGetter(i -> i.trailEffect),
            IParticleConfig.CODEC.fieldOf("onResolvingEffect").forGetter(i -> i.onResolvingEffect)
    ).apply(instance, ParticleTimeline::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleTimeline> STREAM_CODEC = StreamCodec.composite(IParticleConfig.STREAM_CODEC, ParticleTimeline::trailEffect,
            IParticleConfig.STREAM_CODEC,
            ParticleTimeline::onResolvingEffect,
            ParticleTimeline::new);


    public static ParticleTimeline defaultTimeline(){
        return new ParticleTimeline(new TrailConfiguration(GlowParticleData.createData(ParticleColor.defaultParticleColor())), new BurstConfiguration(GlowParticleData.createData(ParticleColor.defaultParticleColor())));
    }

    public IParticleConfig trailEffect;
    public IParticleConfig onResolvingEffect;

    public ParticleTimeline(IParticleConfig trailEffect, IParticleConfig onResolvingEffect){
        this.trailEffect = trailEffect;
        this.onResolvingEffect = onResolvingEffect;
    }

    public IParticleConfig trailEffect(){
        return trailEffect;
    }

    public IParticleConfig onResolvingEffect(){
        return onResolvingEffect;
    }
}
