package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.BaseProperty;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class PropertyButton extends SelectableButton {
    public ParticleConfigWidgetProvider widgetProvider;

    public int nestLevel = 0;
    public Component fullTitle;
    public List<PropertyButton> children = new ArrayList<>();
    public int index;
    public BaseProperty<?> property;

    public PropertyButton(int x, int y, DocAssets.BlitInfo asset, DocAssets.BlitInfo selectedAsset, BaseProperty<?> property, ParticleConfigWidgetProvider widgetProvider, int nestLevel, OnPress onPress) {
        super(x, y, asset, selectedAsset, onPress);
        this.widgetProvider = widgetProvider;
        this.nestLevel = nestLevel;
        this.property = property;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);

        widgetProvider.renderIcon(graphics, x, y, pMouseX, pMouseY, pPartialTick);
        String titleString = widgetProvider.getButtonTitle().getString();
        int maxLength = switch (nestLevel) {
            case 1 -> 17;
            case 2 -> 12;
            default -> 20;
        };
        if (titleString.length() > maxLength + 3) {
            fullTitle = Component.literal(titleString);
            titleString = titleString.substring(0, maxLength + 1).trim() + "...";
        } else {
            fullTitle = null;
        }
        DocClientUtils.drawStringScaled(graphics, Component.literal(titleString), x + 14, y + 3, 0, 0.8f, false);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (fullTitle != null) {
            tooltip.add(fullTitle);
        }
        widgetProvider.getButtonTooltips(tooltip);
        super.getTooltip(tooltip);

    }
}
