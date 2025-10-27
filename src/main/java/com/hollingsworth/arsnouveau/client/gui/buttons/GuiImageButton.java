package com.hollingsworth.arsnouveau.client.gui.buttons;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.client.gui.GuiUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class GuiImageButton extends ANButton {

    public ResourceLocation image;
    public ResourceLocation hoverImage;
    public int u, v, image_width, image_height;
    public Component toolTip;
    public boolean soundDisabled = false;

    public GuiImageButton(int x, int y, DocAssets.BlitInfo blitInfo, Button.OnPress onPress) {
        this(x, y, blitInfo.width(), blitInfo.height(), blitInfo.location(), onPress);
    }

    public GuiImageButton(int x, int y, int w, int h, ResourceLocation image, Button.OnPress onPress) {
        this(x, y, 0, 0, w, h, w, h, image, onPress);
    }

    public GuiImageButton(int x, int y, int u, int v, int w, int h, int image_width, int image_height, String resource_image, Button.OnPress onPress) {
        this(x, y, u, v, w, h, image_width, image_height, ArsNouveau.prefix(resource_image), onPress);
    }

    public GuiImageButton(int x, int y, int u, int v, int w, int h, int image_width, int image_height, ResourceLocation image, Button.OnPress onPress) {
        super(x, y, w, h, Component.empty(), onPress);
        this.x = x;
        this.y = y;
        this.u = u;
        this.v = v;
        this.image_height = image_height;
        this.image_width = image_width;
        this.image = image;
    }

    public GuiImageButton withHoverImage(DocAssets.BlitInfo blitInfo) {
        this.hoverImage = blitInfo.location();
        return this;
    }

    public GuiImageButton withTooltip(Component toolTip) {
        this.toolTip = toolTip;
        return this;
    }


    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (!this.visible) {
            return;
        }
        if (hoverImage != null && GuiUtils.isMouseInRelativeRange(pMouseX, pMouseY, this)) {
            graphics.blit(hoverImage, x, y, u, v, width, height, image_width, image_height);
        } else {
            graphics.blit(image, x, y, u, v, width, height, image_width, image_height);
        }
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (toolTip != null)
            tooltip.add(toolTip);
    }

    @Override
    public void playDownSound(SoundManager pHandler) {
        if (soundDisabled)
            return;
        super.playDownSound(pHandler);
    }
}