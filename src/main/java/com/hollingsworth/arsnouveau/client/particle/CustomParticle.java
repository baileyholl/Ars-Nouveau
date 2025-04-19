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
        this.lifetime = (int) (200 + ParticleUtil.inRange(0, 10));
        this.hasPhysics = false;
        movement = new SpiralMovement();
        movement.init(this);
        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;
    }

    @Override
    public void tick() {
        super.tick();
        movement.tick();
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        movement.render(buffer, renderInfo, partialTicks);
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