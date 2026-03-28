package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;

import java.util.function.Consumer;

public class BookSlider extends ExtendedSlider {


    Consumer<Double> onValueChange;

    public BookSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString) {
        super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, drawString);
    }

    public BookSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString, Consumer<Double> onValueChange) {
        super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, drawString);
        this.onValueChange = onValueChange;
    }

    public BookSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, boolean drawString, Consumer<Double> onValueChange) {
        super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, drawString);
        this.onValueChange = onValueChange;
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
        // blit now requires a RenderPipeline argument.
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ArsNouveau.prefix("textures/gui/sound_bar.png"), this.getX(), this.getY(), 0.0f, 0.0f, this.width, this.height, this.width, this.height);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ArsNouveau.prefix("textures/gui/sound_bar_knob.png"), this.getX() + (int) (this.value * (double) (this.width - 8)), this.getY(), 0.0f, 0.0f, 8, 20, 8, 20);
        int j = 10526880;
        guiGraphics.drawString(font, this.getMessage(), this.getX() + this.width / 4, this.getY() + (this.height - 32) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24, false);
    }
}
