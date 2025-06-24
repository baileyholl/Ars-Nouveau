package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

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
            ResourceLocation key = BuiltInRegistries.PARTICLE_TYPE.getKey(ParticleTypes.SCULK_CHARGE);
            SpriteSet spriteSet = Minecraft.getInstance().particleEngine.spriteSets.get(key);
            this.sprite = spriteSet;
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
            SculkChargeParticle sculkchargeparticle = new SculkChargeParticle(
                    pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprite
            );
            return sculkchargeparticle;
        }
    }
}
