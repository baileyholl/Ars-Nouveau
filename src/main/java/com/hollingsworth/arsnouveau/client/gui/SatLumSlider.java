package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SatLumSlider extends HorizontalSlider {

    Supplier<HSLColor> baseColor;
    boolean isLuminance = false;
    public SatLumSlider(int x, int y, boolean drawString, boolean isLuminance, Supplier<HSLColor> baseColor, Consumer<Double> onValueChange) {
        super(x, y, DocAssets.SLIDER_BAR, DocAssets.SLIDER, isLuminance ? Component.literal("Lightness") : Component.literal("Saturation"), Component.empty(), 0, 1, 0.5, 0.01, 1, drawString, onValueChange);
        this.isLuminance = isLuminance;
        this.baseColor = baseColor;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        DocClientUtils.blit(guiGraphics, sliderAsset, x, y);

        int drawWidth = this.width - 6;
        int drawX = this.x + 3;

        HSLColor baseColor = this.baseColor.get();

        HSLColor leftColor = HSLColor.hsl(baseColor.getHue(), isLuminance ? baseColor.getSaturation() : 0.01, isLuminance ? 0.01 : baseColor.getLightness(), baseColor.getOpacity());
        guiGraphics.vLine(drawX - 1, y+2, y + 4, leftColor.toInt());
        HSLColor rightColor = HSLColor.hsl(baseColor.getHue(), isLuminance ? baseColor.getSaturation() : 1.0, isLuminance ? 1.0 : baseColor.getLightness(),baseColor.getOpacity());
        guiGraphics.vLine(x + width - 3, y+2, y + 4, rightColor.toInt());

        for (int i = 0; i < drawWidth; i++) {
            double val = (i / (double) drawWidth);

            HSLColor color1 = HSLColor.hsl(baseColor.getHue(), isLuminance ? baseColor.getSaturation() : val, isLuminance ? val : baseColor.getLightness(), baseColor.getOpacity());
            guiGraphics.vLine(drawX + i, y  + 1, y + 5, color1.toInt());
        }

        DocClientUtils.blit(guiGraphics, knobAsset, this.x + (int) (this.value * (double) (this.width - this.knobAsset.width())), this.y - 1);
        int j = 10526880;
        if(this.drawString) {
            guiGraphics.drawString(font, this.getMessage(), this.x + this.width / 4, this.y + (this.height - 32) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24, false);
        }
    }
}
