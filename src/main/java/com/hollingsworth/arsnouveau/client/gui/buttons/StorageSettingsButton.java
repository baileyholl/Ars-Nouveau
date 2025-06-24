package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class StorageSettingsButton extends StateButton {
    public StorageSettingsButton(int x, int y, int width, int height, int imageWidth, int imageHeight, int tile, ResourceLocation texture, OnPress pressable) {
        super(x, y, width, height, imageWidth, imageHeight, tile, texture, pressable);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float pt) {
        if (this.visible) {
            graphics.blit(ArsNouveau.prefix("textures/gui/storage_tab1.png"), x, y, 0, 0, 22, 13, 22, 13);
        }
        super.renderWidget(graphics, mouseX, mouseY, pt);
    }
}
