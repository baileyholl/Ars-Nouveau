package com.hollingsworth.arsnouveau.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.world.World;

public class ParticleLine extends SpriteTexturedParticle {
    public float colorR = 0;
    public float colorG = 0;
    public float colorB = 0;
    public float initScale = 0;
    public float initX = 0;
    public float initY = 0;
    public float initZ = 0;
    public float destX = 0;
    public float destY = 0;
    public float destZ = 0;
    protected ParticleLine(World worldIn, double x, double y, double z, double vx, double vy, double vz, float r, float g, float b, float scale, int lifetime, IAnimatedSprite sprite) {
        super(worldIn, x,y,z,0,0,0);
        this.colorR = r;
        this.colorG = g;
        this.colorB = b;
        if (this.colorR > 1.0){
            this.colorR = this.colorR/255.0f;
        }
        if (this.colorG > 1.0){
            this.colorG = this.colorG/255.0f;
        }
        if (this.colorB > 1.0){
            this.colorB = this.colorB/255.0f;
        }
        this.setColor(colorR, colorG, colorB);
        this.maxAge = lifetime;
        this.particleScale = scale;
        this.initScale = scale;
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        this.initX = (float)x;
        this.initY = (float)y;
        this.initZ = (float)z;
        this.destX = (float)vx;
        this.destY = (float)vy;
        this.destZ = (float)vz;
        this.particleAngle = 2.0f*(float)Math.PI;
        this.selectSpriteRandomly(sprite);
    }

    @Override
    public void tick() {
        super.tick();
        if (world.rand.nextInt(6) == 0){
            this.age ++;
        }
        float lifeCoeff = (float)this.age/(float)this.maxAge;
        this.posX = ((1.0f-lifeCoeff)*initX + (lifeCoeff)*destX);
        this.posY = ((1.0f-lifeCoeff)*initY + (lifeCoeff)*destY);
        this.posZ = ((1.0f-lifeCoeff)*initZ + (lifeCoeff)*destZ);
        this.particleScale = initScale-initScale*lifeCoeff;
        this.particleAlpha = 1.0f-lifeCoeff;
        this.prevParticleAngle = particleAngle;
        particleAngle += 1.0f;
    }


    @Override
    public boolean isAlive() {
        return this.age < maxAge;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return RenderTypes.EMBER_RENDER;
    }


    @Override
    public int getBrightnessForRender(float pTicks){
        return 255;
    }
}
