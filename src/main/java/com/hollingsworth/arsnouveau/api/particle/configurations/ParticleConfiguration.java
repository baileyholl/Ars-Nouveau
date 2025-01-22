package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.IParticleCallback;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;

public abstract class ParticleConfiguration implements IParticleCallback {

    public ParticleOptions particleOptions;
    public ParticleConfiguration(ParticleOptions particleOptions){
        this.particleOptions = particleOptions;
    }

    @Override
    public void tick(Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {

    }

    public ParticleOptions particleOptions() {
        return particleOptions;
    }
}
