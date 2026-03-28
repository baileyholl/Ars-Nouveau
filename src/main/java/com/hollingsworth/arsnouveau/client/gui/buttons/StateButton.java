package com.hollingsworth.arsnouveau.client.gui.buttons;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class StateButton extends ANButton {

    public Identifier texture;
    public int tile;
    public int state;
    public int texX = 0;
    public int texY = 0;
    public int imageWidth;
    public int imageHeight;

    public StateButton(int x, int y, int width, int height, int imageWidth, int imageHeight, int tile, Identifier texture, OnPress pressable) {
        super(x, y, width, height, Component.empty(), pressable);
        this.tile = tile;
        this.texture = texture;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    /**
     * Draws this button to the screen.
     * 1.21.11: renderWidget is final in AbstractButton; override renderContents instead.
     */
    @Override
    protected void renderContents(GuiGraphics p_281670_, int mouseX, int mouseY, float pt) {
        if (this.visible) {
            int x = getX();
            int y = getY();
            this.isHovered = mouseX >= x && mouseY >= y && mouseX < x + this.width && mouseY < y + this.height;
            p_281670_.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, texture, x, y, texX + state * width, texY + tile * height, this.width, this.height, imageWidth, imageHeight);
        }
    }
}
