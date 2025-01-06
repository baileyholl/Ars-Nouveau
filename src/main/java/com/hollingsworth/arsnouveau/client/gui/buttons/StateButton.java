package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class StateButton extends ANButton{

    public ResourceLocation texture;
    public int tile;
    public int state;
    public int texX = 0;
    public int texY = 0;
    public int imageWidth;
    public int imageHeight;
    public StateButton(int x, int y, int width, int height, int imageWidth, int imageHeight, int tile, ResourceLocation texture, OnPress pressable) {
        super(x, y, width, height, Component.empty(), pressable);
        this.tile = tile;
        this.texture = texture;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void renderWidget(GuiGraphics p_281670_, int mouseX, int mouseY, float pt) {
        if (this.visible) {
            PoseStack st = p_281670_.pose();
            int x = getX();
            int y = getY();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, texture);
            this.isHovered = mouseX >= x && mouseY >= y && mouseX < x + this.width && mouseY < y + this.height;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            p_281670_.blit(texture, x, y, texX + state * width, texY + tile * height, this.width, this.height, imageWidth, imageHeight);
        }
    }
}
