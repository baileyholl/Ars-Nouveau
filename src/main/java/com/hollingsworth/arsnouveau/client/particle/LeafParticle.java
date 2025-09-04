package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;

public class LeafParticle extends PropParticle {

    float rotSpeed;

    public LeafParticle(PropertyParticleOptions propertyParticleOptions, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(propertyParticleOptions, level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.lifetime = 40 + this.random.nextInt(20); // shorter lifespan
        this.yd = yd * 0.7 - 0.015; // slightly down from projectile
        this.xd = xd + (this.random.nextDouble() - 0.5) * 0.005;
        this.zd = zd + (this.random.nextDouble() - 0.5) * 0.005;
        this.rotSpeed = ((float) Math.random() - 0.5F) * 0.1F;
        this.roll = (float) Math.random() * (float) (Math.PI * 2);
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
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        setColorFromProps();
        if (onGround) {
            return;
        }

        float gravity = 0.005f;
        float swayFrequency = 0.05f;
        float swayAmplitude = 0.01f;
        float bobFrequency = 0.15f;
        float bobAmplitude = 0.0015f;
        float horizontalDrag = 0.79f;
        float verticalDrag = 0.89f;


        float phase = (this.hashCode() % 6283) / 1000.0f;


        double swayX = Mth.sin(this.age * swayFrequency + phase) * swayAmplitude;
        double swayZ = Mth.cos(this.age * swayFrequency + phase + ((float) Math.PI / 2.0f)) * swayAmplitude;
        double bobY = Mth.sin(this.age * bobFrequency + phase) * bobAmplitude;


        this.yd -= gravity;
        this.yd += bobY;
        this.xd += swayX;
        this.zd += swayZ;


        this.xd *= horizontalDrag;
        this.yd *= verticalDrag;
        this.zd *= horizontalDrag;

        this.move(this.xd, this.yd, this.zd);

        if (this.onGround) {
            this.xd *= 0.6;
            this.zd *= 0.6;
        }

        this.oRoll = this.roll;
        float rollFrequency = 0.1f;
        float motionMagnitude = Mth.sqrt((float) (this.xd * this.xd + this.yd * this.yd + this.zd * this.zd));
        float dynamicAmplitude = (float) Mth.clamp(motionMagnitude * 2.5, 0.1, 0.6);
        this.roll = (Mth.sin(this.age * rollFrequency + phase) * dynamicAmplitude) * 10f;
    }

    @Override
    public ParticleColor getDefaultColor() {
        return ParticleColor.GREEN;
    }
}
