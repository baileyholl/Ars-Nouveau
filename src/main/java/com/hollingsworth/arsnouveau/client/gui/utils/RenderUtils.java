package com.hollingsworth.arsnouveau.client.gui.utils;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.client.gui.Color;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RenderUtils {

    private static final RenderType TRANSLUCENT = RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS);

    public static void drawSpellPart(AbstractSpellPart objectToBeDrawn, PoseStack poseStack, int positionX, int positionY, int size, boolean renderTransparent) {
        RenderUtils.drawItemAsIcon(objectToBeDrawn.glyphItem, poseStack, positionX, positionY, size, renderTransparent);
    }

    public static void drawItemAsIcon(Item providedItem, PoseStack poseStack, int positionX, int positionY, int size, boolean renderTransparent) {
        drawItemAsIcon(new ItemStack(providedItem), poseStack, positionX, positionY, size, renderTransparent);
    }

    public static void drawItemAsIcon(ItemStack itemStack, PoseStack poseStack, int positionX, int positionY, int size, boolean renderTransparent) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        //Code stolen from ItemRenderer.renderGuiItem and changed to suit scaled items instead of fixing size to 16
        BakedModel itemBakedModel = itemRenderer.getModel(itemStack, null, null, 0);

        Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        poseStack.pushPose();
        poseStack.translate(positionX, positionY, 100.0F);
        poseStack.translate(8.0D, 8.0D, 0.0D);
        poseStack.scale(1.0F, -1.0F, 1.0F);
        poseStack.scale(size, size, size);
        RenderSystem.applyModelViewMatrix();
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean flag = !itemBakedModel.usesBlockLight();
        if (flag) {
            Lighting.setupForFlatItems();
        }
        if (renderTransparent) {
            itemRenderer.render(itemStack, ItemTransforms.TransformType.GUI, false, poseStack, transparentBuffer(multibuffersource$buffersource), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, itemBakedModel);
        } else {
            itemRenderer.render(itemStack, ItemTransforms.TransformType.GUI, false, poseStack, multibuffersource$buffersource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, itemBakedModel);
        }
        multibuffersource$buffersource.endBatch();
        if (flag) {
            Lighting.setupFor3DItems();
        }

        if (renderTransparent) {
            RenderSystem.depthMask(true);
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        }

        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
        if (renderTransparent) {
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
        }
    }

    private static MultiBufferSource transparentBuffer(MultiBufferSource buffer) {
        return renderType -> new TintedVertexConsumer(buffer.getBuffer(TRANSLUCENT), 1.0f, 1.0f, 1.0f, 0.25f);
    }

    public static void drawTextureFromResourceLocation(ResourceLocation providedResourceLocation, PoseStack stack, int x, int y, int size, boolean renderTransparent) {
        RenderSystem.enableBlend();
        if (renderTransparent) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5f);
        } else {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }

        RenderSystem.setShaderTexture(0, providedResourceLocation);
        GuiComponent.blit(stack, x, y, 0, 0, size, size, size, size);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

    /*
    * Adapted from Eidolon, Elucent
    */
    public static void colorBlit(PoseStack mStack, int x, int y, int uOffset, int vOffset, int width, int height, int textureWidth, int textureHeight, Color color) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        Matrix4f matrix = mStack.last().pose();
        int maxX = x + width, maxY = y + height;
        float minU = (float) uOffset / textureWidth, minV = (float) vOffset / textureHeight;
        float maxU = minU + (float) width / textureWidth, maxV = minV + (float) height / textureHeight;
        int r = color.getRed(), g = color.getGreen(), b = color.getBlue(), alpha = color.getAlpha();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(matrix, (float) x, (float) maxY, 0).uv(minU, maxV).color(r, g, b, alpha).endVertex();
        bufferbuilder.vertex(matrix, (float) maxX, (float) maxY, 0).uv(maxU, maxV).color(r, g, b, alpha).endVertex();
        bufferbuilder.vertex(matrix, (float) maxX, (float) y, 0).uv(maxU, minV).color(r, g, b, alpha).endVertex();
        bufferbuilder.vertex(matrix, (float) x, (float) y, 0).uv(minU, minV).color(r, g, b, alpha).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.disableBlend();
    }

}
