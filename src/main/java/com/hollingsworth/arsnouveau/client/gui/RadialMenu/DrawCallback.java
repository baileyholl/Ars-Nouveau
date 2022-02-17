package com.hollingsworth.arsnouveau.client.gui.RadialMenu;

import com.mojang.blaze3d.vertex.PoseStack;

public interface DrawCallback {
    void accept(Object objectToBeDrawn, PoseStack poseStack, int positionX, int positionY, int size);
}
