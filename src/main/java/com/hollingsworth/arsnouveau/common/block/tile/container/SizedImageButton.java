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
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;


public class SizedImageButton extends ImageButton {
    public final ResourceLocation resourceLocation;
    public final int xTexStart;
    public final int yTexStart;
    public final int xDiffOffset;
    public final int textureWidth;
    public final int textureHeight;
    public final int textureMapWidth;
    public final int textureMapHeight;

    /**
     * A button that supports texture size, as well as a foreground texture
     *
     * @param xIn              the draw position x
     * @param yIn              the draw position y
     * @param widthIn          the button draw width
     * @param heightIn         the button draw height
     * @param textureOffsetX   the x offset in the texture map
     * @param textureOffsetY   the y offset in the texture map
     * @param hoverOffsetX     the x offset for the hover textures
     * @param textureWidth     the x size to take from the texture map
     * @param textureHeight    the y size to take from the texture map
     * @param textureMapWidth  the x size of the texture map.
     * @param textureMapHeight the y size of the texture map.
     * @param resourceLocation the resource location for the textures
     */
    public SizedImageButton(int xIn, int yIn, int widthIn, int heightIn, int textureOffsetX,
                            int textureOffsetY, int hoverOffsetX, int textureWidth, int textureHeight,
                            int textureMapWidth, int textureMapHeight, ResourceLocation resourceLocation,
                            OnPress handler) {
        super(xIn, yIn, widthIn, heightIn, textureOffsetX, textureOffsetY, 0, resourceLocation, handler);
        this.xTexStart = textureOffsetX;
        this.yTexStart = textureOffsetY;
        this.xDiffOffset = hoverOffsetX;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.textureMapWidth = textureMapWidth;
        this.textureMapHeight = textureMapHeight;
        this.resourceLocation = resourceLocation;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, this.resourceLocation);
            int i = this.xTexStart;
            int j = this.yTexStart;
            if (this.isHoveredOrFocused()) {
                i += this.xDiffOffset;
            }
            RenderSystem.enableDepthTest();
            blit(stack, this.x, this.y, this.width, this.height, i, j, this.textureWidth, this.textureHeight, this.textureMapWidth, this.textureMapHeight);

            if (this.isHoveredOrFocused()) {
                this.renderToolTip(stack, mouseX, mouseY);
            }
        }

    }
}
