package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class RenderFlyingItem extends EntityRenderer<EntityFlyingItem> {

    protected RenderFlyingItem(EntityRenderDispatcher renderManager) {
        super(renderManager);
    }

    @Override
    public void render(EntityFlyingItem entityIn, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStack, bufferIn, packedLightIn);
        matrixStack.pushPose();
        matrixStack.scale(0.35f, 0.35f, 0.35F);
        Minecraft.getInstance().getItemRenderer().renderStatic(entityIn.getStack(), ItemTransforms.TransformType.FIXED, 15728880, OverlayTexture.NO_OVERLAY, matrixStack, bufferIn);
        matrixStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(EntityFlyingItem entity) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png");
    }
}
