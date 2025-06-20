package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;

public class ANBubbleParticle extends TextureSheetParticle {
    public ANBubbleParticle(ClientLevel pLevel, double pX, double pY, double pZ, double xSpeed, double ySpeed, double zSpeed) {
        super(pLevel, pX, pY, pZ);
        this.setSize(0.02F, 0.02F);
        this.quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
        this.xd = xSpeed * (double)0.2F + (Math.random() * (double)2.0F - (double)1.0F) * (double)0.02F;
        this.yd = ySpeed * (double)0.2F + (Math.random() * (double)2.0F - (double)1.0F) * (double)0.02F;
        this.zd = zSpeed * (double)0.2F + (Math.random() * (double)2.0F - (double)1.0F) * (double)0.02F;
        this.lifetime = (int)((double)8.0F / (Math.random() * 0.8 + 0.2));
    }

    @Override
    public void tick() {
        super.tick();
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
            ANBubbleParticle bubblecolumnupparticle = new ANBubbleParticle(
                    pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed
            );
            bubblecolumnupparticle.pickSprite(Minecraft.getInstance().particleEngine.spriteSets.get(ArsNouveau.prefix("bubble")));
            return bubblecolumnupparticle;
        }
    }
}
