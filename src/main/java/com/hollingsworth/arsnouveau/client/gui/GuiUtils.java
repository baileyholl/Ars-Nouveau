package com.hollingsworth.arsnouveau.client.gui;

import net.minecraft.client.gui.components.AbstractWidget;

public class GuiUtils {

    public static boolean isMouseInRelativeRange(int mouseX, int mouseY, AbstractWidget widget) {
        return isMouseInRelativeRange(mouseX, mouseY, widget.x, widget.y, widget.getWidth(), widget.getHeight());
    }

    public static boolean isMouseInRelativeRange(int mouseX, int mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }
}
