package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.IntFunction;

public class StorageSettingsButton extends StateButton {
    public IntFunction<Component> tooltip;

    public StorageSettingsButton(int x, int y, int width, int height, int tile, DocAssets.BlitInfo texture, OnPress pressable) {
        super(x, y, width, height, tile, texture, pressable);
    }

    @Deprecated(forRemoval = true)
    public StorageSettingsButton(int x, int y, int width, int height, int imageWidth, int imageHeight, int tile, ResourceLocation texture, OnPress pressable) {
        this(x, y, width, height, tile, new DocAssets.BlitInfo(texture, imageWidth, imageHeight), pressable);
    }

    public StorageSettingsButton withTooltip(IntFunction<Component> tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (this.tooltip != null) {
            tooltip.add(this.tooltip.apply(state));
        }
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float pt) {
        if (this.visible) {
            DocClientUtils.blit(graphics, DocAssets.STORAGE_SETTINGS_TAB, x, y);
        }
        super.renderWidget(graphics, mouseX, mouseY, pt);
    }
}
