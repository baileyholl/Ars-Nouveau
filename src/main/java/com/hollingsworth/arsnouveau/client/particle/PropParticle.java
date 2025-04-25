package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ColorProperty;
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

    public void setColorFromProps(){
        ColorProperty property = options.map.get(ParticlePropertyRegistry.COLOR_PROPERTY.get());
        ParticleColor color = property != null ? property.particleColor : ParticleColor.WHITE;
        float colorR = color.getRed();
        float colorG = color.getGreen();
        float colorB = color.getBlue();
        this.setColor(colorR, colorG, colorB);
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
            particle.pickSprite(this.sprite);
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
