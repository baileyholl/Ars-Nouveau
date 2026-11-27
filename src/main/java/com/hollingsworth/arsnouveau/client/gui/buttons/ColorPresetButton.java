package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.client.gui.Color;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.RainbowParticleColor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ColorPresetButton extends SelectedParticleButton {
    public ParticleColor particleColor;

    public ColorPresetButton(int x, int y, ParticleColor color, OnPress onPress) {
        super(x, y, DocAssets.SPELLSTYLE_BUTTON_12, DocAssets.SPELLSTYLE_BUTTON_SELECTED_12, onPress);
        this.particleColor = color;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        Color color = new Color(particleColor.getColor(), false);
        if (particleColor instanceof RainbowParticleColor) {
            DocClientUtils.blit(graphics, DocAssets.SPELLSTYLE_RAINBOW, x + 2, y + 2);
        } else if (particleColor == ParticleColor.NONE) {
            DocClientUtils.blit(graphics, DocAssets.SPELLSTYLE_BUTTON_12_CLEAR, x, y);
        } else {
            graphics.fill(x + 3, y + 2, x + 9, y + 10, color.getRGB());
            graphics.fill(x + 2, y + 3, x + 10, y + 9, color.getRGB());
        }
        if (!selected) {
            DocClientUtils.blit(graphics, DocAssets.SPELL_STYLE_BUTTON, x, y, 12, 12);
        } else {
            DocClientUtils.blit(graphics, DocAssets.SPELL_STYLE_BUTTON_SELECTED, x, y, 12, 12);
        }
    }

    public Component getHexString() {
        if (particleColor instanceof RainbowParticleColor) {
            return Component.translatable("ars_nouveau.color_rainbow");
        } else {
            return Component.literal(particleColor.toHex());
        }
    }
}
