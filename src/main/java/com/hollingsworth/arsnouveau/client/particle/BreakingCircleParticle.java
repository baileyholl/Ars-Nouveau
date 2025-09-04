package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;

public class BreakingCircleParticle extends PropParticle {

    SpriteSet spriteSet;

    public BreakingCircleParticle(PropertyParticleOptions propertyParticleOptions, SpriteSet spriteSet, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(propertyParticleOptions, level, x, y, z, 0, 0, 0);
        this.lifetime = 6 + this.random.nextInt(4);
        float f = this.random.nextFloat() * 0.6F + 0.4F;
        this.rCol = f;
        this.gCol = f;
        this.bCol = f;
        this.spriteSet = spriteSet;
        this.quadSize = 0.3F * this.random.nextFloat() + 0.01F;
        this.setSpriteFromAge(spriteSet);
        this.lifetime = 6;
    }

    @Override
    public boolean tinted() {
        return true;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        setColorFromProps();
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.spriteSet);
        }
    }

    @Override
    public ParticleColor getDefaultColor() {
        return ParticleColor.GREEN;
    }

    public static class Provider implements net.minecraft.client.particle.ParticleProvider<PropertyParticleOptions> {
        private final SpriteSet sprite;

        public Provider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        public Particle createParticle(
                PropertyParticleOptions pType,
                ClientLevel pLevel,
                double pX,
                double pY,
                double pZ,
                double pXSpeed,
                double pYSpeed,
                double pZSpeed
        ) {
            BreakingCircleParticle circleParticle = new BreakingCircleParticle(
                    pType, sprite, pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed
            );
            return circleParticle;
        }
    }
}
