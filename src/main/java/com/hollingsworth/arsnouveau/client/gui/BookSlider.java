package com.hollingsworth.arsnouveau.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.ProgressOption;
import net.minecraft.client.gui.components.SliderButton;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class BookSlider extends SliderButton {
    public BookSlider(Options pOptions, int pX, int pY, int pWidth, int pHeight, ProgressOption pProgressOption, List<FormattedCharSequence> pTooltip) {
        super(pOptions, pX, pY, pWidth, pHeight, pProgressOption, pTooltip);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, Minecraft pMinecraft, int pMouseX, int pMouseY) {
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = (this.isHoveredOrFocused() ? 2 : 1) * 20;
        this.blit(pPoseStack, this.x + (int)(this.value * (double)(this.width - 8)), this.y, 0, 46 + i, 4, 20);
        this.blit(pPoseStack, this.x + (int)(this.value * (double)(this.width - 8)) + 4, this.y, 196, 46 + i, 4, 20);
    }
}
