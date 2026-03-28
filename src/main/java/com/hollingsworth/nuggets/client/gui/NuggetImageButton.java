package com.hollingsworth.nuggets.client.gui;

import com.hollingsworth.nuggets.client.BlitInfo;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class NuggetImageButton extends BaseButton {

    public Identifier image;
    public Identifier hoveredImage;
    public int u, v, image_width, image_height;
    public boolean soundDisabled = false;
    public int xOffset, yOffset;

    public NuggetImageButton(int x, int y, BlitInfo blitInfo, Button.OnPress onPress) {
        this(x, y, 0, 0, blitInfo.width(), blitInfo.height(), blitInfo.width(), blitInfo.height(), blitInfo.location(), onPress);
        this.xOffset = blitInfo.xOffset();
        this.yOffset = blitInfo.yOffset();
    }

    public NuggetImageButton(int x, int y, int w, int h, Identifier image, Button.OnPress onPress) {
        this(x, y, 0, 0, w, h, w, h, image, onPress);
    }

    public NuggetImageButton(int x, int y, int w, int h, Identifier image, Identifier hoveredImage, Button.OnPress onPress) {
        this(x, y, 0, 0, w, h, w, h, image, hoveredImage, onPress);
    }

    public NuggetImageButton(int x, int y, int u, int v, int w, int h, int image_width, int image_height, Identifier image, Identifier hoveredImage, Button.OnPress onPress) {
        this(x, y, u, v, w, h, image_width, image_height, image, onPress);
        this.hoveredImage = hoveredImage;
    }


    public NuggetImageButton(int x, int y, int u, int v, int w, int h, int image_width, int image_height, Identifier image, Button.OnPress onPress) {
        super(x, y, w, h, Component.empty(), onPress);
        this.u = u;
        this.v = v;
        this.image_height = image_height;
        this.image_width = image_width;
        this.image = image;
        this.hoveredImage = null;
    }

    @Override
    protected void renderContents(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        Identifier image = GuiHelpers.isMouseInRelativeRange(pMouseX, pMouseY, this) && hoveredImage != null ? hoveredImage : this.image;
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, image, getX() + xOffset, getY() + yOffset, u, v, width, height, image_width, image_height);
    }

    @Override
    public void playDownSound(SoundManager pHandler) {
        if (soundDisabled)
            return;
        super.playDownSound(pHandler);
    }
}