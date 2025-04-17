package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.nuggets.client.gui.NuggetImageButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class DropdownParticleButton extends NuggetImageButton {
    Component title;
    ResourceLocation icon;

    public DropdownParticleButton(int x, int y, Component component, DocAssets.BlitInfo asset, ResourceLocation icon, OnPress onPress) {
        super(x, y, asset.width(), asset.height(), asset.location(), onPress);
        this.title = component;
        this.icon = icon;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
        graphics.blit(icon, x + 2, y + 2, 0, 0, 16, 16, 16, 16);
        DocClientUtils.drawStringScaled(graphics, title, x + 14, y + 3, 0, 0.8f, false);
    }
}
