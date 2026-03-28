package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
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
        // MC 1.21.11: RenderSystem.setShader/setShaderColor/enableBlend/defaultBlendFunc/enableDepthTest removed.
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        DocClientUtils.blit(guiGraphics, sliderAsset, getX(), getY());
        DocClientUtils.blit(guiGraphics, knobAsset, this.getX() + (int) (this.value * (double) (this.width - this.knobAsset.width())), this.getY() - 1);
        int j = 10526880;
        if (this.drawString) {
            guiGraphics.drawString(font, this.getMessage(), this.getX() + this.width / 4, this.getY() + (this.height - 32) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24, false);
        }
    }
}
