package com.hollingsworth.arsnouveau.client.gui.utils;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

public class RenderUtils {

    private static final RenderType TRANSLUCENT = RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS);

    public static void drawSpellPart(AbstractSpellPart objectToBeDrawn, GuiGraphics graphics, int positionX, int positionY, int size, boolean renderTransparent, int zIndex) {
        renderFakeItemTransparent(graphics.pose(), objectToBeDrawn.glyphItem.getDefaultInstance(), positionX, positionY, size, 0, renderTransparent, zIndex);
    }

    public static void drawSpellPart(AbstractSpellPart objectToBeDrawn, GuiGraphics graphics, int positionX, int positionY, int size, boolean renderTransparent) {
        renderFakeItemTransparent(graphics.pose(), objectToBeDrawn.glyphItem.getDefaultInstance(), positionX, positionY, size, 0, renderTransparent,150);
    }

    public static void drawItemAsIcon(ItemStack itemStack, GuiGraphics graphics, int positionX, int positionY, int size, boolean renderTransparent) {
        renderFakeItemTransparent(graphics.pose(), itemStack, positionX, positionY, size, 0, renderTransparent,150);
    }

    public static void renderFakeItemTransparent(PoseStack poseStack, ItemStack stack, int x, int y,int scale, int alpha, boolean transparent, int zIndex) {
        if (stack.isEmpty()) {
            return;
        }

        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();

        BakedModel model = renderer.getModel(stack, null, Minecraft.getInstance().player, 0);
        renderItemModel(poseStack, stack, x, y, scale, alpha, model, renderer, transparent, zIndex);
    }

    private static final Matrix4f SCALE_INVERT_Y = new Matrix4f().scaling(1F, -1F, 1F);

    public static void renderItemModel(PoseStack poseStack, ItemStack stack, int x, int y, int scale, int alpha, BakedModel model, ItemRenderer renderer, boolean transparent, int zIndex) {
        poseStack.pushPose();
        poseStack.translate(x + 8F, y + 8F, zIndex);
        poseStack.mulPose(SCALE_INVERT_Y);
        poseStack.scale(scale, scale, scale);

        boolean flatLight = !model.usesBlockLight();
        if (flatLight) {
            Lighting.setupForFlatItems();
        }

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        renderer.render(
                stack,
                ItemDisplayContext.GUI,
                false,
                poseStack,
                transparent && Config.GUI_TRANSPARENCY.get() ? transparentBuffer(buffer) : buffer, // TODO: remove sodium check when sodium is fixed https://github.com/CaffeineMC/sodium-fabric/pull/1842/files
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                model
        );
        buffer.endBatch();

        RenderSystem.enableDepthTest();

        if (flatLight) {
            Lighting.setupFor3DItems();
        }

        poseStack.popPose();
    }


    private static MultiBufferSource transparentBuffer(MultiBufferSource buffer) {
        return renderType -> new TintedVertexConsumer(buffer.getBuffer(TRANSLUCENT), 1.0f, 1.0f, 1.0f, 0.25f);
    }

//    /*
//     * Adapted from Eidolon, Elucent
//     */
//    public static void colorBlit(PoseStack mStack, int x, int y, int uOffset, int vOffset, int width, int height, int textureWidth, int textureHeight, Color color) {
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
//        Matrix4f matrix = mStack.last().pose();
//        int maxX = x + width, maxY = y + height;
//        float minU = (float) uOffset / textureWidth, minV = (float) vOffset / textureHeight;
//        float maxU = minU + (float) width / textureWidth, maxV = minV + (float) height / textureHeight;
//        int r = color.getRed(), g = color.getGreen(), b = color.getBlue(), alpha = color.getAlpha();
//        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
//        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
//        bufferbuilder.addVertex(matrix, (float) x, (float) maxY, 0).uv(minU, maxV).color(r, g, b, alpha).endVertex();
//        bufferbuilder.addVertex(matrix, (float) maxX, (float) maxY, 0).uv(maxU, maxV).color(r, g, b, alpha).endVertex();
//        bufferbuilder.addVertex(matrix, (float) maxX, (float) y, 0).uv(maxU, minV).color(r, g, b, alpha).endVertex();
//        bufferbuilder.addVertex(matrix, (float) x, (float) y, 0).uv(minU, minV).color(r, g, b, alpha).endVertex();
//        BufferUploader.drawWithShader(bufferbuilder.end());
//        RenderSystem.disableBlend();
//    }

}
