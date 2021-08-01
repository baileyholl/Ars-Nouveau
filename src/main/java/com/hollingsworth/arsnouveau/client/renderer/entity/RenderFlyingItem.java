package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

public class RenderFlyingItem extends EntityRenderer<EntityFlyingItem> {

    protected RenderFlyingItem(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public void render(EntityFlyingItem entityIn, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStack, bufferIn, packedLightIn);
        matrixStack.pushPose();
        matrixStack.scale(0.35f, 0.35f, 0.35F);
        Minecraft.getInstance().getItemRenderer().renderStatic(entityIn.getStack(), ItemCameraTransforms.TransformType.FIXED, 15728880, OverlayTexture.NO_OVERLAY, matrixStack, bufferIn);
        matrixStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(EntityFlyingItem entity) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png");
    }
}
