package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.nuggets.client.gui.NuggetMultilLineLabel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class DocClientUtils {

    public static void drawStringScaled(GuiGraphics graphics, Component component, int x, int y, int color, float scale, boolean shadow){
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(x + 3, y, 0);
        poseStack.scale(scale, scale, 1);
        graphics.drawString(Minecraft.getInstance().font, component, 0, 0, color, shadow);
        poseStack.popPose();
    }

    public static void drawHeader(NuggetMultilLineLabel title, GuiGraphics graphics, int x, int y){
        title.renderCenteredNoShadow(graphics, x, y + (title.getLineCount() > 1 ? 3 : 7), 8, 0);
    }

    public static void blit(GuiGraphics graphics, DocAssets.BlitInfo info, int x, int y){
        graphics.blit(info.location(), x, y, info.u(), info.v(), info.width(), info.height(), info.width(), info.height());
    }

    public static void renderIngredientAtAngle(GuiGraphics graphics, int x, int y, float angle, Ingredient ingredient) {
        if (ingredient.isEmpty()) {
            return;
        }

        angle -= 90;
        int radius = 41;
        double xPos = x + nextXAngle(angle, radius);
        double yPos = y + nextYAngle(angle, radius);
        PoseStack ms = graphics.pose();
        ms.pushPose(); // This translation makes it not stuttery. It does not affect the tooltip as that is drawn separately later.
        ms.translate(xPos - (int) xPos, yPos - (int) yPos, 0);
        DocClientUtils.renderIngredient(graphics, (int) xPos, (int) yPos, ingredient);
        ms.popPose();
    }

    public static double nextXAngle(double angle, int radius) {
        return Math.cos(angle * Math.PI / 180D) * radius + 32;
    }

    public static double nextYAngle(double angle, int radius) {
        return Math.sin(angle * Math.PI / 180D) * radius + 32;
    }

    public static void renderIngredient(GuiGraphics graphics, int x, int y, Ingredient ingr) {
        ItemStack[] stacks = ingr.getItems();
        if (stacks.length > 0) {
            DocClientUtils.renderItemStack(graphics, x, y, stacks[(ClientInfo.ticksInGame / 20) % stacks.length]);
        }
    }

    public static void renderItemStack(GuiGraphics graphics, int x, int y, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        Font font = Minecraft.getInstance().font;
        graphics.renderItem(stack, x, y);
        graphics.renderItemDecorations(font, stack, x, y);
    }
}
