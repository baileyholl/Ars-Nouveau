package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleMotion;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class TimelineEntryData {
    public static final MapCodec<TimelineEntryData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ParticleMotion.CODEC.fieldOf("motion").forGetter(i -> i.motion),
            ParticleTypes.CODEC.fieldOf("particleOptions").forGetter(i -> i.particleOptions)
    ).apply(instance, TimelineEntryData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TimelineEntryData> STREAM = StreamCodec.composite(
            ParticleMotion.STREAM_CODEC,
            TimelineEntryData::motion,
            ParticleTypes.STREAM_CODEC,
            TimelineEntryData::particleOptions, TimelineEntryData::new);

    ParticleMotion motion;
    PropertyParticleOptions particleOptions;

    protected TimelineEntryData(ParticleMotion motion, ParticleOptions particleOptions) {
        this.motion = motion;
        this.particleOptions = (PropertyParticleOptions) particleOptions;
    }

    public TimelineEntryData(ParticleMotion motion, PropertyParticleOptions particleOptions){
        this.motion = motion;
        this.particleOptions = particleOptions;
    }

    public ParticleMotion motion(){
        return motion;
    }

    public PropertyParticleOptions particleOptions(){
        return particleOptions;
    }

    public void setMotion(ParticleMotion motion) {
        this.motion = motion;
    }

    public void setOptions(PropertyParticleOptions particleOptions) {
        this.particleOptions = particleOptions;
    }
}
