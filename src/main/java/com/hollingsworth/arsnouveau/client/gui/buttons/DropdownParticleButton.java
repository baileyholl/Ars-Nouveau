package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class DropdownParticleButton extends SelectableButton {
    Component title;
    ResourceLocation icon;

    public DropdownParticleButton(int x, int y, Component component, DocAssets.BlitInfo asset, DocAssets.BlitInfo asset2, ResourceLocation icon, OnPress onPress) {
        super(x, y, asset, asset2, onPress);
        this.title = component;
        this.icon = icon;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
        graphics.blit(icon, x, y, 0, 0, 14, 14, 14, 14);
        DocClientUtils.drawStringScaled(graphics, title, x + 14, y + 3, 0, 0.8f, false);
    }
}
