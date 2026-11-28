package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.client.gui.NineSlicedImage;
import com.hollingsworth.nuggets.client.gui.BaseButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class FramedButton extends BaseButton {
    public boolean selected;
    public NineSlicedImage selectedFrame = DocAssets.SPELL_STYLE_BUTTON_SELECTED;
    public NineSlicedImage frame = DocAssets.SPELL_STYLE_BUTTON;
    int width;
    int height;

    public FramedButton(int x, int y, int width, int height, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress);
        this.width = width;
        this.height = height;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (selected) {
            DocClientUtils.blit(graphics, selectedFrame, x, y, width, height);
        } else {
            DocClientUtils.blit(graphics, frame, x, y, width, height);
        }
    }
}
