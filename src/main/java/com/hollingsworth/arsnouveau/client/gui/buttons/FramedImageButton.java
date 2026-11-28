package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.client.gui.NineSlicedImage;
import com.hollingsworth.nuggets.client.gui.NuggetImageButton;
import net.minecraft.client.gui.GuiGraphics;

public class FramedImageButton extends NuggetImageButton {

    public boolean selected;
    public NineSlicedImage selectedFrame = DocAssets.SPELL_STYLE_BUTTON_SELECTED;
    public NineSlicedImage frame = DocAssets.SPELL_STYLE_BUTTON;
    int width;
    int height;

    public FramedImageButton(int x, int y, DocAssets.BlitInfo blitInfo, int width, int height, OnPress onPress) {
        super(x, y, blitInfo.width(), blitInfo.height(), blitInfo.location(), onPress);
        this.width = width;
        this.height = height;
        xOffset = (width - blitInfo.width()) / 2;
        yOffset = (height - blitInfo.height()) / 2;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (selected) {
            DocClientUtils.blit(graphics, selectedFrame, x, y, width, height);
        } else {
            DocClientUtils.blit(graphics, frame, x, y, width, height);
        }
        super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
    }
}
