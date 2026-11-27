package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.nuggets.client.BlitInfo;
import com.hollingsworth.nuggets.client.gui.NuggetImageButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;

public class SelectedParticleButton extends NuggetImageButton {

    public boolean selected;
    public DocAssets.BlitInfo selectedFrame = DocAssets.SPELLSTYLE_SELECTED_FRAME;

    public SelectedParticleButton(int x, int y, DocAssets.BlitInfo blitInfo, Button.OnPress onPress) {
        this(x, y, blitInfo.width(), blitInfo.height(), blitInfo.location(), onPress);
        xOffset = (selectedFrame.width() - blitInfo.width()) / 2;
        yOffset = (selectedFrame.height() - blitInfo.height()) / 2;
    }

    public SelectedParticleButton(int x, int y, DocAssets.BlitInfo blitInfo, DocAssets.BlitInfo selectedFrame, Button.OnPress onPress) {
        this(x, y, blitInfo.width(), blitInfo.height(), blitInfo.location(), onPress);
        this.selectedFrame = selectedFrame;
        xOffset = (selectedFrame.width() - blitInfo.width()) / 2;
        yOffset = (selectedFrame.height() - blitInfo.height()) / 2;
    }


    public SelectedParticleButton(int x, int y, BlitInfo blitInfo, OnPress onPress) {
        super(x, y, blitInfo, onPress);
    }

    public SelectedParticleButton(int x, int y, int w, int h, ResourceLocation image, OnPress onPress) {
        super(x, y, w, h, image, onPress);
        xOffset = (selectedFrame.width() - w) / 2;
        yOffset = (selectedFrame.height() - h) / 2;
    }


    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (selected) {
            DocClientUtils.blit(graphics, selectedFrame, x, y);
        }
        super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
    }
}
