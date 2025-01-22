package com.hollingsworth.arsnouveau.client.particle;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.behaviors.IParticleMovement;
import com.hollingsworth.arsnouveau.api.particle.behaviors.SpiralMovement;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class CustomParticle extends TextureSheetParticle {

    IParticleMovement movement;

    protected CustomParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        super(pLevel, pX, pY, pZ);
        this.friction = 0.85F;
        this.setSize(0.02F, 0.02F);
        this.quadSize = this.quadSize * (this.random.nextFloat() * 0.6F + 0.2F);
//        this.xd = pXSpeed * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
//        this.yd = pYSpeed * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
//        this.zd = pZSpeed * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
        this.lifetime = (int) (20 + ParticleUtil.inRange(0, 10));
        this.hasPhysics = false;
        movement = new SpiralMovement();
        movement.init(this);
    }

    @Override
    public void tick() {
        super.tick();
        movement.tick(this);
        float angle = age * 0.2f + 0; // You can adjust the increment to control the speed of the helix
        float radius = 0.1f;
        float radiusY = 0.1f;
        // Calculate new positions for the particle in the double helix pattern
        x += Math.cos(angle) * radius;
     //   y += age * radiusY; // Adjust this value to control the vertical spacing
        y += Math.sin(angle) * radius;
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        // Increment the angle to make the helix spiral

        movement.render(buffer, renderInfo, partialTicks);
      //  setPos(Mth.lerp(partialTicks, xo, x), Mth.lerp(partialTicks, yo, y), Mth.lerp(partialTicks, zo, z));
        super.render(buffer, renderInfo, partialTicks);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        public Particle createParticle(
                SimpleParticleType pType,
                ClientLevel pLevel,
                double pX,
                double pY,
                double pZ,
                double pXSpeed,
                double pYSpeed,
                double pZSpeed
        ) {
            CustomParticle bubblecolumnupparticle = new CustomParticle(
                    pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed
            );
            bubblecolumnupparticle.pickSprite(Minecraft.getInstance().particleEngine.spriteSets.get(ArsNouveau.prefix("bubble")));
            return bubblecolumnupparticle;
        }
    }
}