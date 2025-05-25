package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.client.gui.Color;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.client.gui.GuiGraphics;

public class ColorPresetButton extends GuiImageButton {
    ParticleColor particleColor;
    public ColorPresetButton(int x, int y, ParticleColor color, OnPress onPress) {
        super(x, y, DocAssets.SPELLSTYLE_FRAME, onPress);
        this.particleColor = color;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        Color color = new Color(particleColor.getColor(), false);
        graphics.fill(x + 3, y + 2, x + 11,  y + 12, color.getRGB());
        graphics.fill(x + 2, y + 3, x + 12,  y + 11, color.getRGB());
        super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
    }
}
