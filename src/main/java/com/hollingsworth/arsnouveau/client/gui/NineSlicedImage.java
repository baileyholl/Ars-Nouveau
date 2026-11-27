package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import net.minecraft.client.gui.GuiGraphics;

public record NineSlicedImage(DocAssets.BlitInfo blitInfo, int horizontalOffset, int verticalOffset) {

    public void draw(GuiGraphics guiGraphics, int x, int y) {
        draw(guiGraphics, x, y, blitInfo.width(), blitInfo.height());
    }

    public void draw(GuiGraphics guiGraphics, int x, int y, int width, int height) {

        guiGraphics.blit(blitInfo.location(), x, y, 0, 0, horizontalOffset, verticalOffset, blitInfo.width(), blitInfo.height());

        guiGraphics.blit(blitInfo.location(), x + horizontalOffset, y, width - horizontalOffset * 2, verticalOffset, horizontalOffset, 0, blitInfo.width() - horizontalOffset * 2, verticalOffset, blitInfo.width(), blitInfo.height());

        guiGraphics.blit(blitInfo.location(), x + width - horizontalOffset, y, blitInfo.width() - horizontalOffset, 0, horizontalOffset, verticalOffset, blitInfo.width(), blitInfo.height());

        guiGraphics.blit(blitInfo.location(), x, y + height - verticalOffset, 0, blitInfo.height() - verticalOffset, horizontalOffset, verticalOffset, blitInfo.width(), blitInfo.height());

        guiGraphics.blit(blitInfo.location(), x + width - horizontalOffset, y + height - verticalOffset, blitInfo.width() - horizontalOffset, blitInfo.height() - verticalOffset, horizontalOffset, verticalOffset, blitInfo.width(), blitInfo.height());

        guiGraphics.blit(blitInfo.location(), x, y + verticalOffset, horizontalOffset, height - verticalOffset * 2, 0, verticalOffset, horizontalOffset, blitInfo.height() - verticalOffset * 2, blitInfo.width(), blitInfo.height());

        guiGraphics.blit(blitInfo.location(), x + width - horizontalOffset, y + verticalOffset, horizontalOffset, height - verticalOffset * 2, blitInfo.width() - horizontalOffset, verticalOffset, horizontalOffset, blitInfo.height() - verticalOffset * 2, blitInfo.width(), blitInfo.height());

        guiGraphics.blit(blitInfo.location(), x + horizontalOffset, y + height - verticalOffset, width - horizontalOffset * 2, verticalOffset, horizontalOffset, blitInfo.height() - verticalOffset, blitInfo.width() - horizontalOffset * 2, verticalOffset, blitInfo.width(), blitInfo.height());

        guiGraphics.blit(blitInfo.location(), x + horizontalOffset, y + verticalOffset, width - horizontalOffset * 2, height - verticalOffset * 2, horizontalOffset, verticalOffset, blitInfo.width() - horizontalOffset * 2, blitInfo.height() - verticalOffset * 2, blitInfo.width(), blitInfo.height());
    }
}
