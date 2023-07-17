/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */
package com.hollingsworth.arsnouveau.common.book;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;

public class RenderUtil {
    /**
     * Applies the pose stack to the openGL matrix, then renders using @{@link net.minecraft.client.renderer.entity.ItemRenderer#renderAndDecorateItem(ItemStack,
     * int, int)}
     */
    public static void renderAndDecorateItemWithPose(PoseStack poseStack, ItemStack stack, int x, int y) {
        PoseStack glPoseStack = RenderSystem.getModelViewStack();
        glPoseStack.pushPose();
        glPoseStack.mulPoseMatrix(poseStack.last().pose());
        RenderSystem.applyModelViewMatrix();
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, x, y);
        glPoseStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    public static void renderAndDecorateItemAndDecorationsWithPose(PoseStack poseStack, ItemStack stack, Font font, int x, int y) {
        PoseStack glPoseStack = RenderSystem.getModelViewStack();
        glPoseStack.pushPose();
        glPoseStack.mulPoseMatrix(poseStack.last().pose());
        RenderSystem.applyModelViewMatrix();
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, x, y);
        Minecraft.getInstance().getItemRenderer().renderGuiItemDecorations(font, stack, x, y);
        glPoseStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }
}
