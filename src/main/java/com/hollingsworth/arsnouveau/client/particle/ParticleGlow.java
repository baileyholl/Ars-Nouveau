package com.hollingsworth.arsnouveau.client.particle;


import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;
@OnlyIn(Dist.CLIENT)
public class ParticleGlow extends SpriteTexturedParticle {
    public float colorR = 0;
    public float colorG = 0;
    public float colorB = 0;
    public float initScale = 0;
    public float initAlpha = 0;

    public ParticleGlow(ClientWorld worldIn, double x, double y, double z, double vx, double vy, double vz, float r, float g, float b, float a, float scale, int lifetime, IAnimatedSprite sprite) {
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
        this.maxAge = (int)((float)lifetime*0.5f);
        this.particleScale = scale/8;
        this.initScale = scale;
        this.motionX = vx*2.0f;
        this.motionY = vy*2.0f;
        this.motionZ = vz*2.0f;
        this.initAlpha = a;
        this.selectSpriteRandomly(sprite);

    }
    @Override
    public IParticleRenderType getRenderType() {
        return RenderTypes.EMBER_RENDER;
    }


    @Override
    public int getBrightnessForRender(float pTicks){
        return 255;
    }


    @Override
    public void tick(){
        super.tick();

        if (new Random().nextInt(6) == 0){
            this.age++;
        }
        float lifeCoeff = (float)this.age/(float)this.maxAge;
        this.particleScale = initScale-initScale*lifeCoeff;
        this.particleAlpha = initAlpha*(1.0f-lifeCoeff);
        this.prevParticleAngle = particleAngle;
        particleAngle += 1.0f;
    }



    @Override
    public boolean isAlive() {
        return this.age < this.maxAge;
    }
}