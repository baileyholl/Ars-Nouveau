package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.client.gui.Color;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;

import java.util.ArrayList;
import java.util.List;

public class ANSmokeParticle extends TextureSheetParticle {
    public SpriteSet spriteSet;
    public static List<ColorPoint> gradients = new ArrayList<>(){
        {add(new ColorPoint(0, new Color(215, 193, 47)));}
        {add(new ColorPoint(0.19, new Color(178, 88, 36)));}
        {add(new ColorPoint(0.38, new Color(90, 34, 10)));}
        {add(new ColorPoint(0.68, new Color(35, 7, 7)));}
        {add(new ColorPoint(1.0, new Color(31, 31, 31)));}
    };
    ColorGradientInterpolator gradient;
    protected ANSmokeParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.spriteSet = spriteSet;
        this.setSpriteFromAge(spriteSet);
        this.gradient = new ColorGradientInterpolator(() -> (double) this.age / (double)this.lifetime, gradients);
        this.lifetime = this.random.nextInt(40) + 20;
        this.gravity = 3.0E-6F;
        this.xd = xSpeed;
        this.yd = ySpeed + (double)(this.random.nextFloat() / 500.0F);
        this.zd = zSpeed;
        Color currentColor = gradient.getCurrentColor();
        this.setColor(currentColor.getRedAsFloat(), currentColor.getGreenAsFloat(), currentColor.getBlueAsFloat());
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ < this.lifetime && !(this.alpha <= 0.0F)) {
            this.xd = this.xd + (double)(this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1));
            this.zd = this.zd + (double)(this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1));
            this.yd = this.yd - (double)this.gravity;
            this.move(this.xd, this.yd, this.zd);
            if (this.age >= this.lifetime - 60 && this.alpha > 0.01F) {
                this.alpha -= 0.015F;
            }
        } else {
            this.remove();
        }
        setSpriteFromAge(this.spriteSet);
        Color currentColor = gradient.getCurrentColor();
        this.setColor(currentColor.getRedAsFloat(), currentColor.getGreenAsFloat(), currentColor.getBlueAsFloat());
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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
            ANSmokeParticle bubblecolumnupparticle = new ANSmokeParticle(
                    pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, sprite
            );
            bubblecolumnupparticle.pickSprite(this.sprite);
            return bubblecolumnupparticle;
        }
    }
}
