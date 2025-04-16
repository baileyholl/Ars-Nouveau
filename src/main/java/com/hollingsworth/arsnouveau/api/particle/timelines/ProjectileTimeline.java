package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.hollingsworth.arsnouveau.api.particle.configurations.BurstConfiguration;
import com.hollingsworth.arsnouveau.api.particle.configurations.IParticleConfig;
import com.hollingsworth.arsnouveau.api.particle.configurations.TrailConfiguration;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class ProjectileTimeline implements IParticleTimeline{
    public static final MapCodec<ProjectileTimeline> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            IParticleConfig.CODEC.fieldOf("trailEffect").forGetter(i -> i.trailEffect),
            IParticleConfig.CODEC.fieldOf("onResolvingEffect").forGetter(i -> i.onResolvingEffect)
    ).apply(instance, ProjectileTimeline::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ProjectileTimeline> STREAM_CODEC = StreamCodec.composite(IParticleConfig.STREAM_CODEC, ProjectileTimeline::trailEffect,
            IParticleConfig.STREAM_CODEC,
            ProjectileTimeline::onResolvingEffect,
            ProjectileTimeline::new);

    public IParticleConfig trailEffect;
    public IParticleConfig onResolvingEffect;

    public ProjectileTimeline(){
        this(new TrailConfiguration(GlowParticleData.createData(ParticleColor.defaultParticleColor())), new BurstConfiguration(GlowParticleData.createData(ParticleColor.defaultParticleColor())));
    }

    public ProjectileTimeline(IParticleConfig trailEffect, IParticleConfig onResolvingEffect){
        this.trailEffect = trailEffect;
        this.onResolvingEffect = onResolvingEffect;
    }

    public IParticleConfig trailEffect(){
        return trailEffect;
    }

    public IParticleConfig onResolvingEffect(){
        return onResolvingEffect;
    }


    @Override
    public IParticleTimelineType<?> getType() {
        return ParticleTimelineRegistry.PROJECTILE_TIMELINE.get();
    }
}
