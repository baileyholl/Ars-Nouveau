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
    private List<PropertyButton> children = new ArrayList<>();
    public int index;
    public BaseProperty<?> property;
    public PropertyButton parent;
    private boolean expanded = false;
    public boolean showMarkers = true;

    public PropertyButton(int x, int y, DocAssets.BlitInfo asset, DocAssets.BlitInfo selectedAsset, BaseProperty<?> property, ParticleConfigWidgetProvider widgetProvider, int nestLevel, OnPress onPress) {
        super(x, y, asset, selectedAsset, onPress);
        this.widgetProvider = widgetProvider;
        this.nestLevel = nestLevel;
        this.property = property;
    }

    public void setChildren(List<PropertyButton> children) {
        this.children = children;
        for (PropertyButton child : children) {
            child.parent = this;
        }
    }

    public List<PropertyButton> getChildren() {
        return children;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        for (PropertyButton child : children) {
            if (child.expanded != expanded) {
                child.setExpanded(expanded);
            }
        }
        if (parent != null && parent.expanded != expanded) {
            parent.setExpanded(expanded);
        }
    }

    public boolean isExpanded() {
        return expanded;
    }

    public boolean childOf(PropertyButton parent) {
        if (this.parent == null) {
            return false;
        }
        if (this.parent == parent) {
            return true;
        }
        return this.parent.childOf(parent);
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

        if (showMarkers && nestLevel == 0 && !children.isEmpty()) {
            if (expanded) {
                DocClientUtils.blit(graphics, DocAssets.EXPAND_MARKER, x - 7, y + 6);
            } else {
                DocClientUtils.blit(graphics, DocAssets.COLLAPSE_MARKER, x - 6, y + 4);
            }
        }
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
