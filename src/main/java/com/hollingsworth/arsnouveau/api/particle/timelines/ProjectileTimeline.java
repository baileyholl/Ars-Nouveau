package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.configurations.BurstConfiguration;
import com.hollingsworth.arsnouveau.api.particle.configurations.IConfigurableParticle;
import com.hollingsworth.arsnouveau.api.particle.configurations.IConfigurableParticleType;
import com.hollingsworth.arsnouveau.api.particle.configurations.TrailConfiguration;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProjectileTimeline implements IParticleTimeline{
    public static final MapCodec<ProjectileTimeline> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            IConfigurableParticle.CODEC.fieldOf("trailEffect").forGetter(i -> i.trailEffect),
            IConfigurableParticle.CODEC.fieldOf("onResolvingEffect").forGetter(i -> i.onResolvingEffect)
    ).apply(instance, ProjectileTimeline::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ProjectileTimeline> STREAM_CODEC = StreamCodec.composite(IConfigurableParticle.STREAM_CODEC, ProjectileTimeline::trailEffect,
            IConfigurableParticle.STREAM_CODEC,
            ProjectileTimeline::onResolvingEffect,
            ProjectileTimeline::new);

    public static final List<IConfigurableParticleType<?>> TRAIL_OPTIONS = new CopyOnWriteArrayList<>();
    public static final List<IConfigurableParticleType<?>> RESOLVING_OPTIONS = new CopyOnWriteArrayList<>();

    public IConfigurableParticle trailEffect;
    public IConfigurableParticle onResolvingEffect;

    public ProjectileTimeline(){
        this(new TrailConfiguration(GlowParticleData.createData(ParticleColor.defaultParticleColor())), new BurstConfiguration(GlowParticleData.createData(ParticleColor.defaultParticleColor())));
    }

    public ProjectileTimeline(IConfigurableParticle trailEffect, IConfigurableParticle onResolvingEffect){
        this.trailEffect = trailEffect;
        this.onResolvingEffect = onResolvingEffect;
    }

    public IConfigurableParticle trailEffect(){
        return trailEffect;
    }

    public IConfigurableParticle onResolvingEffect(){
        return onResolvingEffect;
    }

    @Override
    public IParticleTimelineType<?> getType() {
        return ParticleTimelineRegistry.PROJECTILE_TIMELINE.get();
    }

    @Override
    public List<TimelineOption> getTimelineOptions() {
        return List.of(new TimelineOption(ArsNouveau.prefix("trail"), this::trailEffect, (setEffect) -> this.trailEffect = setEffect, ImmutableList.copyOf(TRAIL_OPTIONS)),
                new TimelineOption(ArsNouveau.prefix("impact"), this::onResolvingEffect, (setEffect) -> this.onResolvingEffect = setEffect,ImmutableList.copyOf(RESOLVING_OPTIONS)));
    }
}
