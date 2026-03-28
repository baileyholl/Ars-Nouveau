package com.hollingsworth.nuggets.client.rendering;

import com.hollingsworth.arsnouveau.client.gui.Color;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.util.List;

public class RenderHelpers {

    private static final RenderType TRANSLUCENT = RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS);

    public static void drawItemAsIcon(ItemStack itemStack, GuiGraphics graphics, int positionX, int positionY, int size, boolean renderTransparent) {
        if (itemStack.isEmpty()) return;
        var ms = graphics.pose();
        ms.pushMatrix();
        float scaleFactor = size / 16.0f;
        ms.translate(positionX, positionY);
        ms.scale(scaleFactor, scaleFactor);
        graphics.renderItem(itemStack, 0, 0);
        ms.popMatrix();
    }

    /**
     * @deprecated Call {@link #drawItemAsIcon(ItemStack, GuiGraphics, int, int, int, boolean)} instead.
     */
    @Deprecated
    @SuppressWarnings("unused")
    public static void renderFakeItemTransparent(com.mojang.blaze3d.vertex.PoseStack poseStack, ItemStack stack, int x, int y, int scale, int alpha, boolean transparent, int zIndex) {
        // BakedModel API removed in MC 1.21.11 — cannot render without GuiGraphics
        // This overload is kept for binary compatibility; callers should use the GuiGraphics overload
    }

    private static final Matrix4f SCALE_INVERT_Y = new Matrix4f().scaling(1F, -1F, 1F);

    /**
     * Renders a colored texture region using the new MC 1.21.11 GuiGraphics blit API.
     * Requires the GuiGraphics and the texture Identifier explicitly.
     */
    public static void colorBlit(GuiGraphics graphics, Identifier texture, int x, int y, int uOffset, int vOffset, int width, int height, int textureWidth, int textureHeight, Color color) {
        // Blend state is managed by the GUI_TEXTURED pipeline in MC 1.21.11
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

    public static void renderTooltipInternal(GuiGraphics graphics, List<ClientTooltipComponent> pClientTooltipComponents, int pMouseX, int pMouseY, Screen parentScreen) {
        var font = Minecraft.getInstance().font;
        var parentWidth = parentScreen.width;
        var parentHeight = parentScreen.height;
        if (!pClientTooltipComponents.isEmpty()) {
            int i = 0;
            int j = pClientTooltipComponents.size() == 1 ? -2 : 0;

            for (ClientTooltipComponent clienttooltipcomponent : pClientTooltipComponents) {
                int k = clienttooltipcomponent.getWidth(font);
                if (k > i) {
                    i = k;
                }
                j += clienttooltipcomponent.getHeight(font);
            }

            int j2 = pMouseX + 12;
            int k2 = pMouseY - 12;
            if (j2 + i > parentWidth) {
                j2 -= 28 + i;
            }

            if (k2 + j + 6 > parentHeight) {
                k2 = parentHeight - j - 6;
            }

            int backgroundColor = 0xf0100010;
            int borderStart = 0x505000FF;
            int borderEnd = 0x5028007f;

            graphics.fillGradient(j2 - 3, k2 - 4, j2 + i + 3, k2 - 3, backgroundColor, backgroundColor);
            graphics.fillGradient(j2 - 3, k2 + j + 3, j2 + i + 3, k2 + j + 4, backgroundColor, backgroundColor);
            graphics.fillGradient(j2 - 3, k2 - 3, j2 + i + 3, k2 + j + 3, backgroundColor, backgroundColor);
            graphics.fillGradient(j2 - 4, k2 - 3, j2 - 3, k2 + j + 3, backgroundColor, backgroundColor);
            graphics.fillGradient(j2 + i + 3, k2 - 3, j2 + i + 4, k2 + j + 3, backgroundColor, backgroundColor);
            graphics.fillGradient(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + j + 3 - 1, borderStart, borderEnd);
            graphics.fillGradient(j2 + i + 2, k2 - 3 + 1, j2 + i + 3, k2 + j + 3 - 1, borderStart, borderEnd);
            graphics.fillGradient(j2 - 3, k2 - 3, j2 + i + 3, k2 - 3 + 1, borderStart, borderStart);
            graphics.fillGradient(j2 - 3, k2 + j + 2, j2 + i + 3, k2 + j + 3, borderEnd, borderEnd);

            int l1 = k2;
            for (int i2 = 0; i2 < pClientTooltipComponents.size(); ++i2) {
                ClientTooltipComponent comp = pClientTooltipComponents.get(i2);
                comp.renderText(graphics, font, j2, l1);
                l1 += comp.getHeight(font) + (i2 == 0 ? 2 : 0);
            }
            l1 = k2;
            for (int l2 = 0; l2 < pClientTooltipComponents.size(); ++l2) {
                ClientTooltipComponent comp = pClientTooltipComponents.get(l2);
                comp.renderImage(font, j2, l1, i, j, graphics);
                l1 += comp.getHeight(font) + (l2 == 0 ? 2 : 0);
            }
        }
    }
}
