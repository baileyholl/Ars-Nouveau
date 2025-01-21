package com.hollingsworth.arsnouveau.api.particle.configurations;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class ParticleTrail extends ParticleConfiguration {

    public ParticleTrail(ParticleOptions particleOptions) {
        super(particleOptions);
    }

    @Override
    public void tick(Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {
        super.tick(level, x, y, z, prevX, prevY, prevZ);
        RandomSource random = level.random;
        double deltaX = x - prevX;
        double deltaY = y - prevY;
        double deltaZ = z - prevZ;
        double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 6);
        for (double j = 0; j < dist; j++) {
            double coeff = j / dist;
            level.addParticle(this.particleOptions,
                    (float) (prevX + deltaX * coeff),
                    (float) (prevY + deltaY * coeff) + 0.1, (float)
                            (prevZ + deltaZ * coeff),
                    0.0125f * (random.nextFloat() - 0.5f),
                    0.0125f * (random.nextFloat() - 0.5f),
                    0.0125f * (random.nextFloat() - 0.5f));
        }
    }
}
