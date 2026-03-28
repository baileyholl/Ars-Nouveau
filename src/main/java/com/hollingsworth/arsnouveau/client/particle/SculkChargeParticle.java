package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.RandomSource;

public class SculkChargeParticle extends net.minecraft.client.particle.SculkChargeParticle {

    protected SculkChargeParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprite) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, sprite);

        setAlpha(1.0F);
        setParticleSpeed(xSpeed, ySpeed, zSpeed);
        float roll = (float) ParticleUtil.inRange(0.0, 360.0F);
        oRoll = roll;
        this.roll = roll;
        setLifetime(level.random.nextInt(12) + 8);
    }

    public static class Provider implements ParticleProvider<PropertyParticleOptions> {


        private final SpriteSet sprite;

        public Provider(SpriteSet pSprites) {
            // 1.21.11: particleEngine.spriteSets is no longer public; use the passed SpriteSet directly
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
            SculkChargeParticle sculkchargeparticle = new SculkChargeParticle(
                    pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprite
            );
            return sculkchargeparticle;
        }
    }
}
