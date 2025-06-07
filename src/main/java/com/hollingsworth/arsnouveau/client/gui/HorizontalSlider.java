package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;

import java.util.function.Consumer;

public class HorizontalSlider extends ExtendedSlider {


    Consumer<Double> onValueChange;
    DocAssets.BlitInfo sliderAsset;
    DocAssets.BlitInfo knobAsset;


    public HorizontalSlider(int x, int y, DocAssets.BlitInfo sliderAsset, DocAssets.BlitInfo knobAsset, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString) {
        super(x, y, sliderAsset.width(), knobAsset.height(), prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, drawString);
        this.sliderAsset = sliderAsset;
        this.knobAsset = knobAsset;
    }

    public HorizontalSlider(int x, int y, DocAssets.BlitInfo sliderAsset, DocAssets.BlitInfo knobAsset, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString, Consumer<Double> onValueChange) {
        super(x, y, sliderAsset.width(), knobAsset.height(), prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, drawString);
        this.onValueChange = onValueChange;
        this.sliderAsset = sliderAsset;
        this.knobAsset = knobAsset;
    }

    public HorizontalSlider(int x, int y, DocAssets.BlitInfo sliderAsset, DocAssets.BlitInfo knobAsset, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, boolean drawString, Consumer<Double> onValueChange) {
        super(x, y, sliderAsset.width(), knobAsset.height(), prefix, suffix, minValue, maxValue, currentValue, drawString);
        this.onValueChange = onValueChange;
        this.sliderAsset = sliderAsset;
        this.knobAsset = knobAsset;
    }

    @Override
    protected void applyValue() {
        if (this.onValueChange != null) {
            this.onValueChange.accept(this.value);
        }
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
        DocClientUtils.blit(guiGraphics, sliderAsset, x, y);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        DocClientUtils.blit(guiGraphics, knobAsset, this.x + (int) (this.value * (double) (this.width - this.knobAsset.width())), this.y - 1);
        int j = 10526880;
        if(this.drawString) {
            guiGraphics.drawString(font, this.getMessage(), this.x + this.width / 4, this.y + (this.height - 32) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24, false);
        }
    }
}
