package com.hollingsworth.arsnouveau.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public class ParticleSparkle extends TextureSheetParticle {
    public float colorR;
    public float colorG;
    public float colorB;
    public float initScale;
    public float initAlpha = 0;

    protected ParticleSparkle(ClientLevel worldIn, double x, double y, double z, double vx, double vy, double vz, float r, float g, float b, float scale, int lifetime, SpriteSet sprite) {
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
        this.pickSprite(sprite);
    }
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderTypes.EMBER_RENDER;
    }


    @Override
    public int getLightColor(float pTicks){
        return 255;
    }


    @Override
    public void tick(){
        super.tick();
        float lifeCoeff = (float)this.age/(float)this.lifetime;
        this.alpha = 1.0f-lifeCoeff;
    }

    @Override
    public boolean isAlive() {
        return this.age < this.lifetime;
    }
}