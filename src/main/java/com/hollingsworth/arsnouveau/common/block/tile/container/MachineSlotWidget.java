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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

import javax.annotation.Nonnull;
import java.awt.*;

public class MachineSlotWidget {
    //region Fields
    protected int x;
    protected int y;
    protected int guiLeft;
    protected int guiTop;
    protected Minecraft minecraft;
    protected IStorageControllerGuiContainer parent;
    protected MachineReference machine;
    protected Font fontRenderer;
    protected int slotHighlightColor;
    //endregion Fields

    //region Initialization
    public MachineSlotWidget(IStorageControllerGuiContainer parent, @Nonnull MachineReference machine, int x, int y,
                             int guiLeft, int guiTop) {
        this.x = x;
        this.y = y;
        this.guiLeft = guiLeft;
        this.guiTop = guiTop;
        this.parent = parent;
        this.minecraft = Minecraft.getInstance();
        this.machine = machine;
        this.fontRenderer = this.parent.getFontRenderer();
        this.slotHighlightColor = new Color(255, 255, 255, 128).getRGB();
    }
    //endregion Initialization

    //region Getter / Setter
    public MachineReference getMachine() {
        return this.machine;
    }

    public void setMachine(MachineReference machine) {
        this.machine = machine;
    }

    //endregion Getter / Setter

    //region Methods
    public boolean isMouseOverSlot(double mouseX, double mouseY) {
        return this.parent.isPointInRegionController(this.x - this.guiLeft, this.y - this.guiTop, 16, 16, mouseX, mouseY);
    }

    public void drawSlot(PoseStack poseStack, int mx, int my) {
        poseStack.pushPose();
        //render item
        //RenderHelper.setupGuiFlatDiffuseLighting();

        var isMouseOverSlot = this.isMouseOverSlot(mx, my);

        if (isMouseOverSlot)
            this.minecraft.getItemRenderer().renderAndDecorateItem(this.machine.getExtractItemStack(), this.x, this.y);
        else
            this.minecraft.getItemRenderer().renderAndDecorateItem(this.machine.getInsertItemStack(), this.x, this.y);

        if (isMouseOverSlot) {
            RenderSystem.colorMask(true, true, true, false);
            this.parent.drawGradientRect(poseStack, this.x, this.y, this.x + 16, this.y + 16, this.slotHighlightColor,
                    this.slotHighlightColor);
            RenderSystem.colorMask(true, true, true, true);
        }
        poseStack.popPose();
    }

    public void drawTooltip(PoseStack poseStack, int mx, int my) {
        if (this.isMouseOverSlot(mx, my)) {
            this.parent.renderToolTip(poseStack, this.machine, mx, my);
        }
    }
    //endregion Methods
}
