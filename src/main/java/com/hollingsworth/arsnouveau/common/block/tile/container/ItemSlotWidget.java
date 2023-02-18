/*
 * MIT License
 *
 * Copyright 2020 klikli-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */


package com.hollingsworth.arsnouveau.common.block.tile.container;

import com.github.klikli_dev.occultism.util.RenderUtil;
import com.github.klikli_dev.occultism.util.TextUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.awt.*;

public class ItemSlotWidget {
    //region Fields
    protected int x;
    protected int y;
    protected int stackSize;
    protected int guiLeft;
    protected int guiTop;
    protected boolean showStackSize;
    protected Minecraft minecraft;
    protected IStorageControllerGuiContainer parent;
    protected ItemStack stack;
    protected Font fontRenderer;
    protected int slotHighlightColor;
    //endregion Fields

    //region Initialization
    public ItemSlotWidget(IStorageControllerGuiContainer parent, @Nonnull ItemStack stack, int x, int y, int stackSize,
                          int guiLeft, int guiTop, boolean showStackSize) {
        this.x = x;
        this.y = y;
        this.stackSize = stackSize;
        this.guiLeft = guiLeft;
        this.guiTop = guiTop;
        this.showStackSize = showStackSize;
        this.parent = parent;
        this.minecraft = Minecraft.getInstance();
        this.stack = stack;
        this.fontRenderer = this.parent.getFontRenderer();
        this.slotHighlightColor = new Color(255, 255, 255, 128).getRGB();
    }
    //endregion Initialization

    //region Getter / Setter
    public ItemStack getStack() {
        return this.stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public boolean getShowStackSize() {
        return this.showStackSize;
    }

    public void setShowStackSize(boolean showStackSize) {
        this.showStackSize = showStackSize;
    }
    //endregion Getter / Setter

    //region Methods
    public boolean isMouseOverSlot(int mouseX, int mouseY) {
        return this.parent.isPointInRegionController(this.x - this.guiLeft, this.y - this.guiTop, 16, 16, mouseX, mouseY);
    }

    public void drawSlot(PoseStack poseStack, int mx, int my) {
        poseStack.pushPose();
        if (!this.getStack().isEmpty()) {
            //RenderHelper.enableGUIStandardItemLighting();

            if (this.showStackSize) {

                //get amount to show
                String amount = Screen.hasShiftDown() ? Integer.toString(this.stackSize) : TextUtil.formatLargeNumber(
                        this.stackSize);

                //render item overlay
                poseStack.pushPose();
                poseStack.scale(.5f, .5f, .5f);
                this.minecraft.getItemRenderer().blitOffset = 0.1f;
                //copied from ItemRenderer.renderGuiItemDecorations but allows to scale
                RenderUtil.renderGuiItemDecorationsWithPose(this.minecraft.getItemRenderer(), this.fontRenderer, poseStack, this.stack, this.x * 2 + 16, this.y * 2 + 16, amount);
                // this.minecraft.getItemRenderer().renderGuiItemDecorations(this.fontRenderer, this.stack, this.x, this.y, amount);
                poseStack.popPose();
            }

            this.minecraft.getItemRenderer().blitOffset = -100F;
            this.minecraft.getItemRenderer().renderAndDecorateItem(this.getStack(), this.x, this.y);

            if (this.isMouseOverSlot(mx, my)) {
                RenderSystem.colorMask(true, true, true, false);
                this.parent.drawGradientRect(poseStack, this.x, this.y, this.x + 16, this.y + 16, this.slotHighlightColor,
                        this.slotHighlightColor);
                RenderSystem.colorMask(true, true, true, true);
            }
        }

        poseStack.popPose();
    }

    public void drawTooltip(PoseStack poseStack, int mx, int my) {
        if (this.isMouseOverSlot(mx, my) && !this.getStack().isEmpty()) {
            this.parent.renderToolTip(poseStack, this.getStack(), mx, my);
        }
    }
    //endregion Methods
}
