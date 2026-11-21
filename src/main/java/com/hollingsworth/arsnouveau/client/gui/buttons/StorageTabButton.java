package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class StorageTabButton extends StateButton {
    public boolean isSelected;
    public String highlightText;
    public boolean isAll = false;

    @Deprecated(forRemoval = true)
    public StorageTabButton(int x, int y, int width, int height, int tile, DocAssets.BlitInfo texture, OnPress pressable) {
        super(x, y, width, height, tile, texture, pressable);
    }

    public StorageTabButton(int x, int y, int width, int height, int state, int tile, DocAssets.BlitInfo texture, OnPress pressable) {
        super(x, y, width, height, tile, texture, pressable);
        this.state = state;
    }

    @Deprecated(forRemoval = true)
    public StorageTabButton(int x, int y, int width, int height, int imageWidth, int imageHeight, int tile, ResourceLocation texture, OnPress pressable) {
        this(x, y, width, height, tile, new DocAssets.BlitInfo(texture, imageWidth, imageHeight), pressable);
    }

    @Deprecated(forRemoval = true)
    public StorageTabButton(int x, int y, int width, int height, int imageWidth, int imageHeight, int state, int tile, ResourceLocation texture, OnPress pressable) {
        this(x, y, width, height, state, tile, new DocAssets.BlitInfo(texture, imageWidth, imageHeight), pressable);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (isAll) {
            tooltip.add(Component.translatable("tooltip.ars_nouveau.master_tab"));
        } else if (highlightText != null) {
            tooltip.add(Component.literal(highlightText));
        }
    }

    @Override
    public void renderWidget(GuiGraphics p_281670_, int mouseX, int mouseY, float pt) {
        if (this.visible) {
            DocClientUtils.blit(p_281670_, isSelected ? DocAssets.STORAGE_TAB_SELECTED : DocAssets.STORAGE_TAB, x, y);
        }
        super.renderWidget(p_281670_, mouseX, mouseY, pt);
    }
}
