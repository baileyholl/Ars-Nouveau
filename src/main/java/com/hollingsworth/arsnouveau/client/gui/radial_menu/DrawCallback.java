package com.hollingsworth.arsnouveau.client.gui.radial_menu;

import net.minecraft.client.gui.GuiGraphics;

public interface DrawCallback<T> {
    void accept(T objectToBeDrawn, GuiGraphics poseStack, int positionX, int positionY, int size, boolean renderTransparent);
}
