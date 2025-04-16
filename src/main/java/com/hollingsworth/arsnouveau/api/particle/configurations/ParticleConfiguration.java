package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;

/**
 * Spawns particles via callbacks from the emitter
 */
public abstract class ParticleConfiguration implements IConfigurableParticle {

    public ParticleOptions particleOptions;
    public ParticleEmitter emitter;

    public ParticleConfiguration(ParticleOptions particleOptions){
        this.particleOptions = particleOptions;
    }

    @Override
    public void init(ParticleEmitter emitter) {
        this.emitter = emitter;
    }

    @Override
    public void tick(Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {

    }

    public ParticleOptions particleOptions() {
        return particleOptions;
    }
}
