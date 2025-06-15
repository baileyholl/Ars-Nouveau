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

public class HueSlider extends HorizontalSlider {

    Supplier<HSLColor> baseColor;

    public HueSlider(int x, int y, boolean drawString, Supplier<HSLColor> baseColor, Consumer<Double> onValueChange) {
        super(x, y, DocAssets.SLIDER_BAR, DocAssets.SLIDER, Component.literal("Hue"), Component.empty(), 0, 360, 1, 1, 1, drawString, onValueChange);
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

        HSLColor leftColor = HSLColor.hsl(1, baseColor.getSaturation(), baseColor.getLightness(), baseColor.getOpacity());

        guiGraphics.vLine(drawX - 1, y+2, y + 4, leftColor.toInt());
        HSLColor rightColor = HSLColor.hsl(359, baseColor.getSaturation(), baseColor.getLightness(),baseColor.getOpacity());
        guiGraphics.vLine(x + width - 3, y+2, y + 4, rightColor.toInt());
        for(int i = 0; i < drawWidth; i++) {
            HSLColor color1 = HSLColor.hsl((double) (i * 360) / drawWidth, baseColor.getSaturation(), baseColor.getLightness(), baseColor.getOpacity());
            guiGraphics.vLine(drawX + i, y  + 1, y + 5, color1.toInt());
        }
        DocClientUtils.blit(guiGraphics, knobAsset, this.x + (int) (this.value * (double) (this.width - this.knobAsset.width())), this.y - 1);
        int j = 10526880;
        if(this.drawString) {
            guiGraphics.drawString(font, this.getMessage(), this.x + this.width / 4, this.y + (this.height - 32) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24, false);
        }
    }
}
