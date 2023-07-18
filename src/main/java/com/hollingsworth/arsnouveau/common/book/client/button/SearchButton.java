/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.client.button;

import com.hollingsworth.arsnouveau.common.book.client.BookOverviewScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class SearchButton extends Button {

    private final BookOverviewScreen parent;
    private final int scissorX;

    public SearchButton(BookOverviewScreen parent, int pX, int pY, int scissorX, int width, int height, OnPress pOnPress, Tooltip tooltip) {
        super(pX, pY, width, height, Component.literal(""), pOnPress, Button.DEFAULT_NARRATION);
        this.setTooltip(tooltip);
        this.scissorX = scissorX;
        this.parent = parent;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        if (this.visible) {
            guiGraphics.pose().pushPose();
            int xOffset = this.parent.getBook().getSearchButtonXOffset();
            guiGraphics.pose().translate(xOffset, 0, 0);

            int scissorX = this.scissorX + xOffset;
            int texX = 15;
            int texY = 165;

            int renderX = this.getX();
            int scissorWidth = this.width + (this.getX() - this.scissorX);
            int scissorY = (this.parent.height - this.getY() - this.height - 1); //from the bottom up

            if (this.isHovered()) {
                renderX += 1;
                scissorWidth -= 1;
            }

            //as of 1.20 this causes the button to vanish behind the rendered world, so we don't use it
            //guiGraphics.pose().translate(xOffset, 0, -1000);

            //GL scissors allows us to move the button on hover without intersecting with book border
            guiGraphics.enableScissor(scissorX, scissorY, scissorX + scissorWidth, scissorY + 1000);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            guiGraphics.blit(this.parent.getBookOverviewTexture(), renderX, this.getY(), texX, texY, this.width, this.height, 256, 256);

            guiGraphics.disableScissor();

            guiGraphics.pose().popPose();

        }
    }
}
