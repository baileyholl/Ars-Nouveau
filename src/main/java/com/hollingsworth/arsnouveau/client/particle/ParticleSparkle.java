package com.hollingsworth.arsnouveau.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;

public class ParticleSparkle extends SpriteTexturedParticle {
    public float colorR = 0;
    public float colorG = 0;
    public float colorB = 0;
    public float initScale = 0;
    public float initAlpha = 0;

    protected ParticleSparkle(ClientWorld worldIn, double x, double y, double z, double vx, double vy, double vz, float r, float g, float b, float scale, int lifetime, IAnimatedSprite sprite) {
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
        this.lifetime = lifetime;
        this.quadSize = scale;
        this.hasPhysics = false;
        this.initScale = scale;
        this.xd = ParticleUtil.inRange(-0.01, 0.01);
        this.yd = -0.02;
        this.zd = ParticleUtil.inRange(-0.01, 0.01);
//        this.particleAngle = 2.0f*(float)Math.PI;
        this.pickSprite(sprite);
    }
    @Override
    public IParticleRenderType getRenderType() {
        return RenderTypes.EMBER_RENDER;
    }


    @Override
    public int getLightColor(float pTicks){
        return 255;
    }


    @Override
    public void tick(){
        super.tick();

//        if (new Random().nextInt(6) == 0){
//            this.age++;
//        }

        float lifeCoeff = (float)this.age/(float)this.lifetime;
//        this.particleScale = initScale-initScale*lifeCoeff;
        this.alpha = 1.0f-lifeCoeff;
//        float lifeCoeff = (float)this.age/(float)this.maxAge;
//        this.particleScale = initScale-initScale*lifeCoeff;
//        this.particleAlpha = initAlpha*(1.0f-lifeCoeff);
//        this.prevParticleAngle = particleAngle;
//        particleAngle += 1.0f;
    }

    @Override
    public boolean isAlive() {
        return this.age < this.lifetime;
    }
}