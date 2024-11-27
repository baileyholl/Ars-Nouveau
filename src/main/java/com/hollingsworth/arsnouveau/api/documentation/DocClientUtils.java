package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class DocClientUtils {

    public static void blit(GuiGraphics graphics, DocAssets.BlitInfo info, int x, int y){
        graphics.blit(info.location(), x, y, info.u(), info.v(), info.width(), info.height(), info.width(), info.height());
    }

    public static void renderIngredientAtAngle(GuiGraphics graphics, int x, int y, float angle, Ingredient ingredient) {
        if (ingredient.isEmpty()) {
            return;
        }

        angle -= 90;
        int radius = 32;
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
