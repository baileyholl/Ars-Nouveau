package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.Identifier;

public class SelectableButton extends GuiImageButton {
    public Identifier secondImage;
    public boolean isSelected;
    public Identifier originalImage;

    public SelectableButton(int x, int y, int u, int v, int w, int h, int image_width, int image_height, Identifier resource_image, Identifier secondImage, Button.OnPress onPress) {
        super(x, y, u, v, w, h, image_width, image_height, resource_image.getPath(), onPress);
        this.secondImage = secondImage;
        this.originalImage = resource_image;
    }

    public SelectableButton(int x, int y, DocAssets.BlitInfo asset, DocAssets.BlitInfo secondAsset, Button.OnPress onPress) {
        this(x, y, asset.u(), asset.v(), asset.width(), asset.height(), asset.width(), asset.height(), asset.location(), secondAsset.location(), onPress);
    }

    @Override
    protected void renderContents(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.image = isSelected ? secondImage : originalImage;
        super.renderContents(graphics, pMouseX, pMouseY, pPartialTick);
    }
}
