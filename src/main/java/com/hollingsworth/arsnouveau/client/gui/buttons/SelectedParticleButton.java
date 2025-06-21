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

    public SelectedParticleButton(int x, int y, DocAssets.BlitInfo blitInfo, Button.OnPress onPress) {
        this(x, y, blitInfo.width(), blitInfo.height(), blitInfo.location(), onPress);
        xOffset = (DocAssets.SPELLSTYLE_SELECTED_FRAME.width() - blitInfo.width()) / 2;
        yOffset = (DocAssets.SPELLSTYLE_SELECTED_FRAME.height() - blitInfo.height()) / 2;
    }


    public SelectedParticleButton(int x, int y, BlitInfo blitInfo, OnPress onPress) {
        super(x, y, blitInfo, onPress);
    }

    public SelectedParticleButton(int x, int y, int w, int h, ResourceLocation image, OnPress onPress) {
        super(x, y, w, h, image, onPress);
        xOffset = (DocAssets.SPELLSTYLE_SELECTED_FRAME.width() - w) / 2;
        yOffset = (DocAssets.SPELLSTYLE_SELECTED_FRAME.height() - h) / 2;
    }

    public SelectedParticleButton(int x, int y, int w, int h, ResourceLocation image, ResourceLocation hoveredImage, OnPress onPress) {
        super(x, y, w, h, image, hoveredImage, onPress);
    }

    public SelectedParticleButton(int x, int y, int u, int v, int w, int h, int image_width, int image_height, ResourceLocation image, ResourceLocation hoveredImage, OnPress onPress) {
        super(x, y, u, v, w, h, image_width, image_height, image, hoveredImage, onPress);
    }

    public SelectedParticleButton(int x, int y, int u, int v, int w, int h, int image_width, int image_height, ResourceLocation image, OnPress onPress) {
        super(x, y, u, v, w, h, image_width, image_height, image, onPress);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (selected) {
            DocClientUtils.blit(graphics, DocAssets.SPELLSTYLE_SELECTED_FRAME, x, y);
        }
        super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
    }
}
