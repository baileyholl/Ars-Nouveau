package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;

public class SnowParticle extends TextureSheetParticle {
    private static final float ACCELERATION_SCALE = 0.0025F;
    private static final int INITIAL_LIFETIME = 300;
    private static final int CURVE_ENDPOINT_TIME = 300;
    private static final float FALL_ACC = 0.25F;
    private static final float WIND_BIG = 2.0F;
    private float rotSpeed;
    private final float particleRandom;
    private final float spinAcceleration;

    protected SnowParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.setSprite(spriteSet.get(this.random.nextInt(12), 12));
        this.rotSpeed = (float)Math.toRadians(this.random.nextBoolean() ? -30.0 : 30.0);
        this.particleRandom = this.random.nextFloat();
        this.spinAcceleration = (float)Math.toRadians(this.random.nextBoolean() ? -5.0 : 5.0);
        this.lifetime = 300;
        this.gravity = 7.5E-4F;
        float f = this.random.nextBoolean() ? 0.05F : 0.075F;
        this.quadSize = f;
        this.setSize(f, f);
        this.friction = 1.0F;

        this.gravity = 0.025f;
        this.friction = 0.98f;
        float scalar = 0.03f;
        this.xd = 0 + ParticleUtil.inRange(-scalar, scalar);
        this.yd = 0 +ParticleUtil.inRange(-scalar, scalar);
        this.zd = 0 + ParticleUtil.inRange(-scalar, scalar);
        this.quadSize = 0.06F * (this.random.nextFloat() * this.random.nextFloat() * 1.0F + 1.0F);
        this.lifetime = (int) ParticleUtil.inRange(20, 30);

    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        this.age++;
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.lifetime-- <= 0) {
            this.remove();
        }

        if (!this.removed) {
            float f = (float)(300 - this.lifetime);
            float f1 = Math.min(f / 300.0F, 1.0F);
            double d0 = Math.cos(Math.toRadians((double)(this.particleRandom * 60.0F))) * 2.0 * Math.pow((double)f1, 1.25);
            double d1 = Math.sin(Math.toRadians((double)(this.particleRandom * 60.0F))) * 2.0 * Math.pow((double)f1, 1.25);
//            this.xd += d0 * 0.0025F;
//            this.zd += d1 * 0.0025F;
            this.yd = this.yd - (double)this.gravity * 0.08f;
            this.rotSpeed = this.rotSpeed + this.spinAcceleration / 20.0F;
            this.oRoll = this.roll;
            this.roll = this.roll + this.rotSpeed / 20.0F;
            this.move(this.xd, this.yd, this.zd);
            if (this.onGround || this.lifetime < 299 && (this.xd == 0.0 || this.zd == 0.0)) {
                this.remove();
            }

            if (!this.removed) {
                this.xd = this.xd * (double)this.friction;
                this.yd = this.yd * (double)this.friction;
                this.zd = this.zd * (double)this.friction;
            }
            if (this.age >= this.lifetime - 10 && this.alpha > 0.01F) {
                this.alpha -= 0.015F;
                this.scale(0.95f);
            }
        }
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
                double pZSpeed
        ) {
            SnowParticle snowParticle = new SnowParticle(
                    pLevel, pX, pY, pZ, sprite
            );
            snowParticle.pickSprite(this.sprite);
            return snowParticle;
        }
    }
}
