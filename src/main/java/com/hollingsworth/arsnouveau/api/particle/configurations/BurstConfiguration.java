package com.hollingsworth.arsnouveau.api.particle.configurations;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;

public class BurstConfiguration extends ParticleConfiguration{
    public BurstConfiguration(ParticleOptions particleOptions) {
        super(particleOptions);
    }

    @Override
    public void tick(Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        super.tick(level, x, y, z, prevX, prevY, prevZ);
        for (int i = 0; i < 10; i++) {
            double d0 = x + 0.5;
            double d1 = y + 1.2;
            double d2 = z + .5;
            level.addParticle(this.particleOptions, d0, d1, d2,
                    (level.random.nextFloat() - 0.5) / 3.0,
                    (level.random.nextFloat() - 0.5) / 3.0,
                    (level.random.nextFloat() - 0.5) / 3.0);
        }
    }
}
