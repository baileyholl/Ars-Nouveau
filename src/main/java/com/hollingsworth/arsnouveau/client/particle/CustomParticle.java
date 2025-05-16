package com.hollingsworth.arsnouveau.client.particle;


import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class CustomParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    protected CustomParticle(ClientLevel pLevel, SpriteSet sprites, double pX, double pY, double pZ, double xSpeed, double ySpeed, double zSpeed) {
        super(pLevel, pX, pY, pZ);
        double quadSizeMultiplier = 1.0f;
        this.friction = 0.96F;
        this.gravity = gravity;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = sprites;
        this.xd *= (double)0.1;
        this.yd *= (double)-0.1;
        this.zd *= (double)0.1;
        this.xd += xSpeed;
        this.yd += ySpeed;
        this.zd += zSpeed;
        float f = level.random.nextFloat() * 0.5F;
        this.rCol = f;
        this.gCol = f;
        this.bCol = f;
        this.quadSize *= 0.75F * quadSizeMultiplier;
        this.lifetime = (int)((double)lifetime / ((double)level.random.nextFloat() * 0.8 + 0.2) * quadSizeMultiplier);
        this.lifetime = Math.max(this.lifetime, 1);
        this.setSpriteFromAge(sprites);
        this.hasPhysics = hasPhysics;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        return this.quadSize * Mth.clamp(((float)this.age + scaleFactor) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        super.render(buffer, renderInfo, partialTicks);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
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
            SpriteSet spriteSet = Minecraft.getInstance().particleEngine.spriteSets.get(ResourceLocation.withDefaultNamespace("white_ash"));
            CustomParticle bubblecolumnupparticle = new CustomParticle(
                    pLevel,spriteSet, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed
            );
            return bubblecolumnupparticle;
        }
    }
}