package com.hollingsworth.nuggets.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

public interface ITooltipRenderer {
    default void gatherTooltips(GuiGraphics stack, int mouseX, int mouseY, List<Component> tooltip){
        gatherTooltips(tooltip);
    }

    void gatherTooltips(List<Component> tooltip);
}
