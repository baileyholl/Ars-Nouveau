package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.nuggets.client.gui.NuggetImageButton;
import net.minecraft.client.gui.GuiGraphics;

public class PropertyButton extends NuggetImageButton {
    public ParticleConfigWidgetProvider widgetProvider;

    public PropertyButton(int x, int y, DocAssets.BlitInfo asset, ParticleConfigWidgetProvider widgetProvider, OnPress onPress) {
        super(x, y, asset.width(), asset.height(), asset.location(), onPress);
        this.widgetProvider = widgetProvider;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
        widgetProvider.renderIcon(graphics, x, y, pMouseX, pMouseY, pPartialTick);
        DocClientUtils.drawStringScaled(graphics, widgetProvider.getButtonTitle(), x + 14, y + 3, 0, 0.8f, false);
    }
}
