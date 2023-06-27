package com.hollingsworth.arsnouveau.client.gui.utils;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.client.gui.Color;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.MatrixUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Matrix4f;

public class RenderUtils {

    private static final RenderType TRANSLUCENT = RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS);

    public static void drawSpellPart(AbstractSpellPart objectToBeDrawn, GuiGraphics graphics, int positionX, int positionY, int size, boolean renderTransparent) {
        RenderUtils.drawItemAsIcon(objectToBeDrawn.glyphItem, graphics, positionX, positionY, size, renderTransparent);
    }

    public static void drawItemAsIcon(Item providedItem, GuiGraphics graphics, int positionX, int positionY, int size, boolean renderTransparent) {
        drawItemAsIcon(new ItemStack(providedItem), graphics, positionX, positionY, size, renderTransparent);
    }

    public static void drawItemAsIcon(ItemStack itemStack, GuiGraphics graphics, int positionX, int positionY, int size, boolean renderTransparent) {
        if(itemStack.isEmpty()) return;
        PoseStack pose = graphics.pose();
        BakedModel bakedModel = Minecraft.getInstance().getItemRenderer().getModel(itemStack, null, null, 0);
        pose.pushPose();
        pose.translate(positionX, positionY, 0);
        pose.translate(8.0D, 8.0D, 0.0D);
        pose.mulPoseMatrix((new Matrix4f()).scaling(1.0F, -1.0F, 1.0F));
        pose.scale(size, size, size);
        boolean flag = !bakedModel.usesBlockLight();
        if (flag) {
            Lighting.setupForFlatItems();
        }

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        // == copied from ItemRenderer.render
        ItemDisplayContext displayContext = ItemDisplayContext.GUI;
        MultiBufferSource bufferSource =  Minecraft.getInstance().renderBuffers().bufferSource();
        int light = 15728880;
        int overlay = OverlayTexture.NO_OVERLAY;
        boolean lefthandTransform = false;
        pose.pushPose();
        if (renderTransparent) {
            bufferSource = transparentBuffer(bufferSource);
        }
        if (itemStack.is(Items.TRIDENT)) {
            bakedModel = itemRenderer.getItemModelShaper().getModelManager().getModel(ModelResourceLocation.vanilla("trident", "inventory"));
        } else if (itemStack.is(Items.SPYGLASS)) {
            bakedModel = itemRenderer.getItemModelShaper().getModelManager().getModel(ModelResourceLocation.vanilla("spyglass", "inventory"));
        }


        bakedModel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(pose, bakedModel, displayContext, lefthandTransform);
        pose.translate(-0.5F, -0.5F, -0.5F);
        if (!bakedModel.isCustomRenderer() && (!itemStack.is(Items.TRIDENT) || flag)) {
            for (var model : bakedModel.getRenderPasses(itemStack, true)) {
                for (var rendertype : model.getRenderTypes(itemStack, true)) {
                    VertexConsumer vertexconsumer;
                    if (itemStack.is(ItemTags.COMPASSES) || itemStack.is(Items.CLOCK) && itemStack.hasFoil()) {
                        pose.pushPose();
                        PoseStack.Pose posestack$pose = pose.last();
                        MatrixUtil.mulComponentWise(posestack$pose.pose(), 0.5F);


                        vertexconsumer = ItemRenderer.getCompassFoilBufferDirect(bufferSource, rendertype, posestack$pose);

                        pose.popPose();
                    } else {
                        vertexconsumer = ItemRenderer.getFoilBufferDirect(bufferSource, rendertype, true, itemStack.hasFoil());
                    }

                    itemRenderer.renderModelLists(model, itemStack, light, overlay, pose, vertexconsumer);
                }
            }
        } else {
            net.minecraftforge.client.extensions.common.IClientItemExtensions.of(itemStack).getCustomRenderer().renderByItem(itemStack, displayContext, pose, bufferSource, light, overlay);
        }
        pose.popPose();
        graphics.flush();
        if (flag) {
            Lighting.setupFor3DItems();
        }

        if (renderTransparent) {
            RenderSystem.depthMask(true);
        }

        pose.popPose();
    }


    private static MultiBufferSource transparentBuffer(MultiBufferSource buffer) {
        return renderType -> new TintedVertexConsumer(buffer.getBuffer(TRANSLUCENT), 1.0f, 1.0f, 1.0f, 0.25f);
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
