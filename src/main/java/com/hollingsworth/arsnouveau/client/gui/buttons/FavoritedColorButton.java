package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.documentation.DocPlayerData;
import com.hollingsworth.arsnouveau.client.gui.Color;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.RainbowParticleColor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

public class FavoritedColorButton extends FramedButton {
    public ParticleColor particleColor;
    public int index;

    public FavoritedColorButton(int x, int y, int index, ParticleColor color, OnPress onPress) {
        super(x, y, 12, 12, onPress);
        this.particleColor = color;
        this.index = index;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 1) {
            this.particleColor = null;
            DocPlayerData.favoriteColors.set(index, null);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
        if (particleColor instanceof RainbowParticleColor) {
            DocClientUtils.blit(graphics, DocAssets.SPELLSTYLE_RAINBOW, x + 2, y + 2);
        } else if (particleColor == null) {
            DocClientUtils.blit(graphics, DocAssets.SPELLSTYLE_BUTTON_12_ADD, x, y);
        } else {
            Color color = new Color(particleColor.getColor(), false);
            graphics.fill(x + 3, y + 2, x + 9, y + 10, color.getRGB());
            graphics.fill(x + 2, y + 3, x + 10, y + 9, color.getRGB());
        }

    }

    @Override
    public void gatherTooltips(List<Component> tooltip) {
        super.gatherTooltips(tooltip);
        tooltip.add(particleColor == null ? Component.translatable("ars_nouveau.click_to_add") : Component.translatable("ars_nouveau.right_click_to_remove"));
    }
}
