package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class RenderRitualProjectile extends RenderBlank{
    public RenderRitualProjectile(EntityRendererProvider.Context renderManager, ResourceLocation entityTexture) {
        super(renderManager, entityTexture);
    }

    @Override
    public void render(Entity entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }
}
