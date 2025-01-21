package com.hollingsworth.arsnouveau.api.particle;

import com.hollingsworth.arsnouveau.api.particle.configurations.BurstConfiguration;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfiguration;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleTrail;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;

public class ParticleTimeline {

    public static ParticleTimeline defaultTimeline(){
        return new ParticleTimeline(new ParticleTrail(GlowParticleData.createData(ParticleColor.defaultParticleColor())), new BurstConfiguration(GlowParticleData.createData(ParticleColor.defaultParticleColor())));
    }

    public ParticleConfiguration trailEffect;
    public ParticleConfiguration onResolvingEffect;

    public ParticleTimeline(ParticleConfiguration trailEffect, ParticleConfiguration onResolvingEffect){
        this.trailEffect = trailEffect;
        this.onResolvingEffect = onResolvingEffect;
    }
}
