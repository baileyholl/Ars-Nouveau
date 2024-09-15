package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

public class RenderFlyingItem extends EntityRenderer<EntityFlyingItem> {

    public RenderFlyingItem(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(EntityFlyingItem entityIn, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStack, bufferIn, packedLightIn);
        if(entityIn.getEntityData().get(EntityFlyingItem.IS_BUBBLE)){
            BubbleRenderer.renderBubble(entityIn, this.entityRenderDispatcher, matrixStack, bufferIn);
            return;
        }
        matrixStack.pushPose();
        matrixStack.scale(0.35f, 0.35f, 0.35F);
        Minecraft.getInstance().getItemRenderer().renderStatic(entityIn.getStack(), ItemDisplayContext.FIXED, 15728880, OverlayTexture.NO_OVERLAY, matrixStack, bufferIn, entityIn.level, (int) entityIn.blockPosition().asLong());
        matrixStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(EntityFlyingItem entity) {
        return ArsNouveau.prefix( "textures/entity/spell_proj.png");
    }
}
