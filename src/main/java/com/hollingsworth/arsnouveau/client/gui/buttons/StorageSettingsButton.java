package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

public class StorageSettingsButton extends StateButton {
    public StorageSettingsButton(int x, int y, int width, int height, int imageWidth, int imageHeight, int tile, Identifier texture, OnPress pressable) {
        super(x, y, width, height, imageWidth, imageHeight, tile, texture, pressable);
    }

    @Override
    protected void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float pt) {
        if (this.visible) {
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, ArsNouveau.prefix("textures/gui/storage_tab1.png"), x, y, 0, 0, 22, 13, 22, 13);
        }
        super.renderContents(graphics, mouseX, mouseY, pt);
    }
}
