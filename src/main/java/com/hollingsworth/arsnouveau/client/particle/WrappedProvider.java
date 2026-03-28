package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ColorProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ParticleTypeProperty;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

// 1.21.11: particleEngine.spriteSets no longer public. Accept SpriteSet explicitly.
// Particle.setColor(float,float,float) removed — tinting is not available in this port.
public class WrappedProvider implements ParticleProvider<PropertyParticleOptions> {

    private final SpriteSet sprite;
    @SuppressWarnings("rawtypes")
    public ParticleProvider particleProvider;
    public SimpleParticleType originalType;

    // Constructor for ParticleProvider<?> wrapping (no extra sprite lookup needed)
    public WrappedProvider(ParticleType<?> originalType, @SuppressWarnings("rawtypes") ParticleProvider<?> particleProvider) {
        this.sprite = null;
        this.particleProvider = particleProvider;
    }

    // Constructor for sprite-based providers: takes SpriteSet factory from registerSpriteSet callback.
    // Uses Function<SpriteSet, ParticleProvider<?>> to avoid wildcard inference issues with SpriteParticleRegistration.
    @SuppressWarnings("unchecked")
    public WrappedProvider(SpriteSet spriteSet, Function<SpriteSet, ? extends ParticleProvider<?>> factory) {
        this.sprite = spriteSet;
        this.particleProvider = factory.apply(spriteSet);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable Particle createParticle(PropertyParticleOptions data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
        Particle particle = particleProvider.createParticle(originalType, level, x, y, z, xSpeed, ySpeed, zSpeed, random);
        if (particle == null) return null;
        // TODO: 1.21.11 - Particle.setColor(float,float,float) removed. Particle tinting via
        // PropertyParticleOptions color is not supported in this port.
        var particleData = ParticleTypeProperty.PARTICLE_TYPES.get(data.getType());
        if (particleData != null && particleData.acceptsColor()) {
            ColorProperty colorProperty = data.colorProp();
            if (!colorProperty.isTintDisabled()) {
                // Color tinting stubbed: setColor removed in 1.21.11
            }
        }
        return particle;
    }
}
