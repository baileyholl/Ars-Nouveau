package com.hollingsworth.arsnouveau.client.gui.buttons;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;

public class SelectableButton extends GuiImageButton {
    public ResourceLocation secondImage;
    public boolean isSelected;

    public SelectableButton(int x, int y, int u, int v, int w, int h, int image_width, int image_height, ResourceLocation resource_image, ResourceLocation secondImage, Button.OnPress onPress) {
        super(x, y, u, v, w, h, image_width, image_height, resource_image.getPath(), onPress);
        this.secondImage = secondImage;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.image = isSelected ? secondImage : image;
        super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
    }
}
