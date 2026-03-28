package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.EmitterProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ParticleTypeProperty;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;

public abstract class PropParticle extends SingleQuadParticle {
    PropertyParticleOptions options;

    protected PropParticle(PropertyParticleOptions options, ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite) {
        this(options, level, x, y, z, 0, 0, 0, sprite);
    }

    protected PropParticle(PropertyParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, TextureAtlasSprite sprite) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, sprite);
        this.options = options;
        if (tinted()) {
            setColorFromProps();
        }
    }

    @Override
    public void tick() {
        super.tick();
        setColorFromProps();
    }

    public void setColorFromProps() {
        ParticleColor particleColor = getPropColor();
        if (particleColor == null) {
            return;
        }
        setColorFromParticleColor(particleColor);
    }

    public @Nullable ParticleColor getPropColor() {
        ParticleTypeProperty property = options.map.get(ParticlePropertyRegistry.TYPE_PROPERTY.get());
        if (property != null && property.getColor().isTintDisabled()) {
            return getDefaultColor();
        } else {
            return property != null ? property.getParticleColor() : getDefaultColor();
        }
    }

    public void setColorFromParticleColor(ParticleColor color) {
        if (color == null) {
            return;
        }
        EmitterProperty emitterProp = options.map.get(ParticlePropertyRegistry.EMITTER_PROPERTY.get());
        if (emitterProp != null) {
            color = color.transition(emitterProp.age + age * 50);
        } else {
            color = color.transition((int) (level.getGameTime() % 20 + age * 50));
        }
        float colorR = color.getRed();
        float colorG = color.getGreen();
        float colorB = color.getBlue();
        this.setColor(colorR, colorG, colorB);
    }

    public @Nullable ParticleColor getDefaultColor() {
        return null;
    }

    @Override
    public SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.OPAQUE;
    }

    public boolean tinted() {
        return false;
    }

    public static class Provider implements net.minecraft.client.particle.ParticleProvider<PropertyParticleOptions> {
        private final SpriteSet sprite;
        PropParticle.ParticleProvider<PropertyParticleOptions> particleConstructor;

        public Provider(PropParticle.ParticleProvider<PropertyParticleOptions> particleConstructor, SpriteSet pSprites) {
            this.sprite = pSprites;
            this.particleConstructor = particleConstructor;
        }

        public Provider(SpriteSet pSprites, PropParticle.ParticleProvider<PropertyParticleOptions> particleConstructor) {
            this.sprite = pSprites;
            this.particleConstructor = particleConstructor;
        }

        public Particle createParticle(
                PropertyParticleOptions pType,
                ClientLevel pLevel,
                double pX,
                double pY,
                double pZ,
                double pXSpeed,
                double pYSpeed,
                double pZSpeed,
                RandomSource random
        ) {
            TextureAtlasSprite selectedSprite = this.sprite != null ? this.sprite.get(random) : null;
            return this.particleConstructor.createParticle(pType, pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, selectedSprite);
        }
    }

    @FunctionalInterface
    public interface ParticleProvider<T extends PropertyParticleOptions> {
        @Nullable
        Particle createParticle(
                T type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, @Nullable TextureAtlasSprite sprite
        );
    }
}
