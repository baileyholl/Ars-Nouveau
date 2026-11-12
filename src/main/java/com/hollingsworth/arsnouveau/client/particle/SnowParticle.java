package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;

public class SnowParticle extends PropParticle {
    private float rotSpeed;
    private final float spinAcceleration;

    public SnowParticle(PropertyParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(options, level, x, y, z);
        this.rotSpeed = (float) Math.toRadians(this.random.nextBoolean() ? -30.0 : 30.0);
        this.spinAcceleration = (float) Math.toRadians(this.random.nextBoolean() ? -5.0 : 5.0);
        this.lifetime = 300;
        this.gravity = 7.5E-4F;
        float f = this.random.nextBoolean() ? 0.05F : 0.075F;
        this.quadSize = f;
        this.setSize(f, f);
        this.friction = 1.0F;

        this.gravity = 0.025f;
        this.friction = 0.98f;
        float scalar = 0.03f;
        this.xd = xSpeed + ParticleUtil.inRange(-scalar, scalar);
        this.yd = ySpeed + ParticleUtil.inRange(-scalar, scalar);
        this.zd = zSpeed + ParticleUtil.inRange(-scalar, scalar);
        this.quadSize = 0.06F * (this.random.nextFloat() * this.random.nextFloat() * 1.0F + 1.0F);
        this.lifetime = (int) ParticleUtil.inRange(20, 30);

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
        this.age++;
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.lifetime-- <= 0) {
            this.remove();
        }

        if (!this.removed) {
            setColorFromProps();
            this.yd = this.yd - (double) this.gravity * 0.08f;
            this.rotSpeed = this.rotSpeed + this.spinAcceleration / 20.0F;
            this.oRoll = this.roll;
            this.roll = this.roll + this.rotSpeed / 20.0F;
            this.move(this.xd, this.yd, this.zd);
            if (this.onGround || this.lifetime < 299 && (this.xd == 0.0 || this.zd == 0.0)) {
                this.remove();
            }

            if (!this.removed) {
                this.xd = this.xd * (double) this.friction;
                this.yd = this.yd * (double) this.friction;
                this.zd = this.zd * (double) this.friction;
            }
            if (this.age >= this.lifetime - 10 && this.alpha > 0.01F) {
                this.alpha -= 0.015F;
                this.scale(0.95f);
            }
        }
    }
}
