package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import org.jetbrains.annotations.Nullable;

public class NewGlowParticleProvider implements ParticleProvider<PropertyParticleOptions> {

    private final SpriteSet sprite;

    public NewGlowParticleProvider(SpriteSet pSprites) {
        this.sprite = pSprites;
    }

    @Override
    public @Nullable Particle createParticle(PropertyParticleOptions data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        ParticleColor color = data.map.has(ParticlePropertyRegistry.TYPE_PROPERTY.get()) ? data.map.get(ParticlePropertyRegistry.TYPE_PROPERTY.get()).getColor().particleColor : ParticleColor.defaultParticleColor();
        return new ParticleGlow(level, x, y, z, xSpeed, ySpeed, zSpeed, color.getRed(), color.getGreen(), color.getBlue(), 1.0f, 0.25f, 36, this.sprite, false);
    }
}
