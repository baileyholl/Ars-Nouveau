package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;

public class ANBubbleParticle extends SingleQuadParticle {
    public ANBubbleParticle(ClientLevel pLevel, double pX, double pY, double pZ, double xSpeed, double ySpeed, double zSpeed, TextureAtlasSprite sprite) {
        super(pLevel, pX, pY, pZ, sprite);
        this.setSize(0.02F, 0.02F);
        this.quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
        this.xd = xSpeed * (double) 0.2F + (Math.random() * (double) 2.0F - (double) 1.0F) * (double) 0.02F;
        this.yd = ySpeed * (double) 0.2F + (Math.random() * (double) 2.0F - (double) 1.0F) * (double) 0.02F;
        this.zd = zSpeed * (double) 0.2F + (Math.random() * (double) 2.0F - (double) 1.0F) * (double) 0.02F;
        this.lifetime = (int) ((double) 8.0F / (Math.random() * 0.8 + 0.2));
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.OPAQUE;
    }

    public static class Provider implements ParticleProvider<PropertyParticleOptions> {
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
                double pZSpeed,
                RandomSource random
        ) {
            return new ANBubbleParticle(
                    pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, sprite.get(random)
            );
        }
    }
}
