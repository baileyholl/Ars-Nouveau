package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;

public class RenderRitualProjectile extends RenderBlank{
    protected RenderRitualProjectile(EntityRenderDispatcher renderManager, ResourceLocation entityTexture) {
        super(renderManager, entityTexture);
    }

    @Override
    public void render(Entity entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
//        World world = entity.getEntityWorld();
//        EntityRitualProjectile entityIn = (EntityRitualProjectile) entity;
//        int counter = 0;
//        for (double j = 0; j < 5; j++) {
//
//            counter += world.rand.nextInt(3);
//            if (counter % (Minecraft.getInstance().gameSettings.particles.getId() == 0 ? 1 : 2 * Minecraft.getInstance().gameSettings.particles.getId()) == 0) {
//                world.addParticle(GlowParticleData.createData(entityIn.getParticleColor()), (float) (entityIn.getPositionVec().getX()), (float) (entityIn.getPositionVec().getY()),
//                        (float) (getPositionVec().getZ()), 0.0125f * (world.rand.nextFloat() - 0.5f), 0.0125f * (world.rand.nextFloat()), 0.0125f * (world.rand.nextFloat() ));
//            }
//        }
       // entityIn.setPosition(Math.sin((ClientInfo.ticksInGame + partialTicks) /10D)/9d + entityIn.getPosX(), entityIn.getPosY(), Math.cos((ClientInfo.ticksInGame  + partialTicks)/10D)/9d + entityIn.getPosZ());
    }
}
