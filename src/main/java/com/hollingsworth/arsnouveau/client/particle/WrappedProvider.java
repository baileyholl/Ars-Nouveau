package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import org.jetbrains.annotations.Nullable;

public class WrappedProvider implements ParticleProvider<PropertyParticleOptions> {

    private final SpriteSet sprite;
    public ParticleProvider particleProvider;
    public WrappedProvider(SpriteSet pSprites, ParticleProvider<?> particleProvider) {
        this.sprite = pSprites;
        this.particleProvider = particleProvider;
    }

    @Override
    public @Nullable Particle createParticle(PropertyParticleOptions data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return particleProvider.createParticle(data, level, x, y, z, xSpeed, ySpeed, zSpeed);
    }
}

