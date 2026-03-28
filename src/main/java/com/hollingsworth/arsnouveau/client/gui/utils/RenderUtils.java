package com.hollingsworth.arsnouveau.client.gui.utils;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.client.gui.Color;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;

public class RenderUtils {

    public static void drawSpellPart(AbstractSpellPart objectToBeDrawn, GuiGraphics graphics, int positionX, int positionY, int size, boolean renderTransparent, int zIndex) {
        drawItemAsIcon(objectToBeDrawn.glyphItem.getDefaultInstance(), graphics, positionX, positionY, size, renderTransparent);
    }

    public static void drawSpellPart(AbstractSpellPart objectToBeDrawn, GuiGraphics graphics, int positionX, int positionY, int size, boolean renderTransparent) {
        drawItemAsIcon(objectToBeDrawn.glyphItem.getDefaultInstance(), graphics, positionX, positionY, size, renderTransparent);
    }

    public static void drawItemAsIcon(ItemStack itemStack, GuiGraphics graphics, int positionX, int positionY, int size, boolean renderTransparent) {
        if (itemStack.isEmpty()) return;
        // MC 1.21.11: graphics.pose() returns Matrix3x2fStack (no Z, no PoseStack).
        // TODO: 1.21.11 - pose transforms not available (Z-translate and 3D scale removed).
        // Render item at scaled position by adjusting the destination coordinates directly.
        float scaleFactor = size / 16.0f;
        int renderX = (int) (positionX / scaleFactor);
        int renderY = (int) (positionY / scaleFactor);
        var ms = graphics.pose();
        ms.pushMatrix();
        ms.scale(scaleFactor, scaleFactor);
        graphics.renderItem(itemStack, renderX, renderY);
        ms.popMatrix();
    }

    /**
     * @deprecated Call {@link #drawItemAsIcon(ItemStack, GuiGraphics, int, int, int, boolean)} instead.
     */
    @Deprecated
    public static void renderFakeItemTransparent(PoseStack poseStack, ItemStack stack, int x, int y, int scale, int alpha, boolean transparent, int zIndex) {
        // BakedModel API removed in MC 1.21.11 — cannot render without GuiGraphics
        // This overload is kept for binary compatibility; callers should use GuiGraphics overload
    }

    /**
     * Renders a colored texture region. Texture must be a full atlas texture that GuiGraphics can blit.
     * In MC 1.21.11, color tinting uses RenderPipelines.GUI_TEXTURED with an ARGB color.
     */
    public static void colorBlit(GuiGraphics graphics, Identifier texture, int x, int y, int uOffset, int vOffset, int width, int height, int textureWidth, int textureHeight, Color color) {
        // MC 1.21.11: RenderSystem blend calls removed — GuiGraphics handles blending internally.
        int argb = ARGB.color(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, (float) uOffset, (float) vOffset, width, height, textureWidth, textureHeight, argb);
    }

    /**
     * @deprecated Pass GuiGraphics and texture Identifier explicitly.
     */
    @Deprecated
    public static void colorBlit(PoseStack mStack, int x, int y, int uOffset, int vOffset, int width, int height, int textureWidth, int textureHeight, Color color) {
        // Cannot render without texture Identifier in MC 1.21.11 — no-op stub.
        // Callers should migrate to colorBlit(GuiGraphics, Identifier, ...).
    }

    public static void drawString(String string, GuiGraphics guiGraphics, int positionX, int positionY, int size, boolean renderTransparent) {
        guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable(string), positionX, positionY, Color.WHITE.getRGB());
    }
}
