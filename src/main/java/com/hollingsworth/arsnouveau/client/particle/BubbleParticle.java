package com.hollingsworth.arsnouveau.client.particle;


import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class BubbleParticle extends SingleQuadParticle {

    protected BubbleParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, TextureAtlasSprite sprite) {
        super(pLevel, pX, pY, pZ, sprite);
        this.friction = 0.85F;
        this.setSize(0.02F, 0.02F);
        this.quadSize = this.quadSize * (this.random.nextFloat() * 0.6F + 0.2F);
        this.xd = pXSpeed;// * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
        this.yd = pYSpeed;// * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
        this.zd = pZSpeed;// * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
        this.lifetime = 35;
        this.hasPhysics = false;
    }

    @Override
    public SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.OPAQUE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        public Particle createParticle(
                SimpleParticleType pType,
                ClientLevel pLevel,
                double pX,
                double pY,
                double pZ,
                double pXSpeed,
                double pYSpeed,
                double pZSpeed,
                RandomSource random
        ) {
            return new BubbleParticle(
                    pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, sprite.get(random)
            );
        }
    }
}
