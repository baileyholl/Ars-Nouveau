package com.hollingsworth.arsnouveau.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.item.ItemFrameEntity;

public class GuiScrollHUD extends AbstractGui {

    private static final Minecraft minecraft = Minecraft.getInstance();

    public void drawHUD(MatrixStack matrixStack, ItemFrameEntity entity){
//        matrixStack.push();
//        matrixStack.translate(0.5D, 0.55f, 0.5D);
//        matrixStack.scale(0.35f, 0.35f, 0.35F);
//        Minecraft.getInstance().getItemRenderer().renderItem(entity.getDisplayedItem(), ItemCameraTransforms.TransformType.FIXED, 15728880, overlayIn, matrixStack, IRenderTypeBuffer.Impl);
//        matrixStack.pop();
    }
}
