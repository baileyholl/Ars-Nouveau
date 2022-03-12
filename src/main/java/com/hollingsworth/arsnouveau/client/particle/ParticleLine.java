package com.hollingsworth.arsnouveau.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.world.level.Level;

public class ParticleLine extends TextureSheetParticle {
    public float colorR;
    public float colorG;
    public float colorB;
    public float initScale;
    public float initX;
    public float initY;
    public float initZ;
    public float destX;
    public float destY;
    public float destZ;
    protected ParticleLine(Level worldIn, double x, double y, double z, double vx, double vy, double vz, float r, float g, float b, float scale, int lifetime, SpriteSet sprite) {
        super((ClientLevel) worldIn, x,y,z,0,0,0);
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
        this.initScale = scale;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.initX = (float)x;
        this.initY = (float)y;
        this.initZ = (float)z;
        this.destX = (float)vx;
        this.destY = (float)vy;
        this.destZ = (float)vz;
        this.roll = 2.0f*(float)Math.PI;
        this.pickSprite(sprite);
    }

    @Override
    public void tick() {
        super.tick();
        if (level.random.nextInt(6) == 0){
            this.age ++;
        }
        float lifeCoeff = (float)this.age/(float)this.lifetime;
        this.x = ((1.0f-lifeCoeff)*initX + (lifeCoeff)*destX);
        this.y = ((1.0f-lifeCoeff)*initY + (lifeCoeff)*destY);
        this.z = ((1.0f-lifeCoeff)*initZ + (lifeCoeff)*destZ);
        this.quadSize = initScale-initScale*lifeCoeff;
        this.alpha = 1.0f-lifeCoeff;
        this.oRoll = roll;
//        particleAngle += 1.0f;
    }


    @Override
    public boolean isAlive() {
        return this.age < lifetime;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderTypes.EMBER_RENDER;
    }


    @Override
    public int getLightColor(float pTicks){
        return 255;
    }
}
