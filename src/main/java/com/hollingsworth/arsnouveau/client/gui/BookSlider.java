package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;

public class BookSlider extends ExtendedSlider {


    public BookSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString) {
        super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, drawString);
    }

    public BookSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, boolean drawString) {
        super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, drawString);
    }

    @Override
    public void renderTexture(GuiGraphics p_283546_, ResourceLocation p_281674_, int p_281808_, int p_282444_, int p_283651_, int p_281601_, int p_283472_, int p_282390_, int p_281441_, int p_281711_, int p_281541_) {
        super.renderTexture(p_283546_, p_281674_, p_281808_, p_282444_, p_283651_, p_281601_, p_283472_, p_282390_, p_281441_, p_281711_, p_281541_);
    }

    @Override
    protected void applyValue() {

    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        guiGraphics.blit(ArsNouveau.prefix( "textures/gui/sound_bar.png"), this.x, this.y, 0, 0, 100, 20, this.width, this.height);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(ArsNouveau.prefix( "textures/gui/sound_bar_knob.png"), this.x + (int) (this.value * (double) (this.width - 8)), this.y, 0, 0, 8, 20, 8, 20);
        int j = 10526880;
        guiGraphics.drawString(font, this.getMessage(), this.x + this.width / 4, this.y + (this.height - 32) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24, false);
    }
}
