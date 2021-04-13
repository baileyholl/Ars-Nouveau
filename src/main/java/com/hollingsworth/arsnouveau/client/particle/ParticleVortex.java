package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.world.ClientWorld;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class ParticleVortex extends ParticleGlow{
    int randOffset;
    double randMotionOff;
    public ParticleVortex(ClientWorld worldIn, double x, double y, double z, double vx, double vy, double vz, float r, float g, float b, float a, float scale, int lifetime, IAnimatedSprite sprite) {
        super(worldIn, x, y, z, vx, vy, vz, r, g, b, a, scale, lifetime, sprite);
        this.particleScale = scale/15f;
        initScale = scale/2.0f;
        this.maxAge = (int) ParticleUtil.inRange(25, 90);
        this.canCollide = false;
        randOffset = (int) ParticleUtil.inRange(300, 1000);
        randMotionOff = ParticleUtil.inRange(0.1, 0.5);
    }


    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        } else {
            this.motionY -= 0.04D * (double)this.particleGravity;
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)0.98F;
            this.motionY *= (double)0.98F;
            this.motionZ *= (double)0.98F;
            if (this.onGround) {
                this.motionX *= (double)0.7F;
                this.motionZ *= (double)0.7F;
            }

        }
        float radius = 0.5f;
        float lifeCoeff = (float)this.age/(float)this.maxAge;
        float reduc = 15f;
        this.motionX =   randMotionOff * sin(ClientInfo.ticksInGame + randOffset)/(reduc);
        this.motionY = -0.005;
        this.motionZ =   randMotionOff * cos(ClientInfo.ticksInGame + randOffset)/(reduc);

//        this.posX -= Math.sin(ClientInfo.ticksInGame/)/15D + ParticleUtil.inRange(-0.05, 0.05);
//        this.posY -= Math.log(age)/200.0;
//        this.posZ -= Math.cos(ClientInfo.ticksInGame)/15D + ParticleUtil.inRange(-0.05, 0.05);


        this.particleScale = initScale-initScale*lifeCoeff;
       // this.particleAlpha = (float) (initAlpha*(1.0f-lifeCoeff));
        this.prevParticleAngle = particleAngle;
       // particleAngle += 1.0f;
    }
}
