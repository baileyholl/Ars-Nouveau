package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.EmitterProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ParticleTypeProperty;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

import javax.annotation.Nullable;

public abstract class PropParticle extends TextureSheetParticle {
    PropertyParticleOptions options;

    protected PropParticle(PropertyParticleOptions options, ClientLevel level, double x, double y, double z) {
        this(options, level, x, y, z, 0, 0, 0);
    }

    protected PropParticle(PropertyParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.options = options;
        if(tinted()){
            setColorFromProps();
        }
    }

    @Override
    public void tick() {
        super.tick();
        setColorFromProps();
    }

    public void setColorFromProps(){
        ParticleColor particleColor = getPropColor();
        if(particleColor == null){
            return;
        }
        setColorFromParticleColor(particleColor);
    }

    public @Nullable ParticleColor getPropColor(){
        ParticleTypeProperty property = options.map.get(ParticlePropertyRegistry.TYPE_PROPERTY.get());
        if(property != null && property.getColor().isTintDisabled()){
            return getDefaultColor();
        } else {
            return property != null ? property.getColor().particleColor : getDefaultColor();
        }
    }

    public void setColorFromParticleColor(ParticleColor color){
        if(color == null){
            return;
        }
        EmitterProperty emitterProp = options.map.get(ParticlePropertyRegistry.EMITTER_PROPERTY.get());
        color = color.transition(emitterProp.age + age * 50);
        float colorR = color.getRed();
        float colorG = color.getGreen();
        float colorB = color.getBlue();
        this.setColor(colorR, colorG, colorB);
    }

    public @Nullable ParticleColor getDefaultColor(){
        return null;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public boolean tinted(){
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

        public TextureSheetParticle createParticle(
                PropertyParticleOptions pType,
                ClientLevel pLevel,
                double pX,
                double pY,
                double pZ,
                double pXSpeed,
                double pYSpeed,
                double pZSpeed
        ) {
            TextureSheetParticle particle = this.particleConstructor.createParticle(pType, pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
            if(this.sprite != null) {
                particle.pickSprite(this.sprite);
            }
            return particle;
        }
    }
    @FunctionalInterface
    public interface ParticleProvider<T extends PropertyParticleOptions> {
        @Nullable
        TextureSheetParticle createParticle(
                T type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
        );
    }
}
