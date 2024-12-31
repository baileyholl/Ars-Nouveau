package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.GuiUtils;
import com.hollingsworth.arsnouveau.client.gui.documentation.IndexScreen;
import com.hollingsworth.arsnouveau.client.gui.documentation.PageHolderScreen;
import com.hollingsworth.nuggets.client.gui.GuiHelpers;
import com.hollingsworth.nuggets.client.gui.NuggetMultilLineLabel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;

public class DocClientUtils {

    public static void openBook(){
        if(DocPlayerData.previousScreen != null){
            Minecraft.getInstance().setScreen(DocPlayerData.previousScreen);
            return;
        }
        IndexScreen.open();
    }

    public static void openToEntry(ResourceLocation resourceLocation, int pageIndex){

        DocEntry entry = DocumentationRegistry.getEntry(resourceLocation);
        if(entry != null){
            PageHolderScreen pageHolderScreen = new PageHolderScreen(entry);
            pageHolderScreen.arrowIndex = pageIndex < entry.pages().size() ? pageIndex : 0;
            Minecraft.getInstance().setScreen(pageHolderScreen);
            return;
        }

        IndexScreen.open();
    }

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

    public static ItemStack renderIngredientAtAngle(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float angle, Ingredient ingredient) {
        if (ingredient.isEmpty()) {
            return ItemStack.EMPTY;
        }

        angle -= 90;
        int radius = 41;
        double xPos = x + nextXAngle(angle, radius);
        double yPos = y + nextYAngle(angle, radius);
        PoseStack ms = graphics.pose();
        ms.pushPose(); // This translation makes it not stuttery. It does not affect the tooltip as that is drawn separately later.
        ms.translate(xPos - (int) xPos, yPos - (int) yPos, 0);
        ItemStack hovered = DocClientUtils.renderIngredient(graphics, (int) xPos, (int) yPos, mouseX, mouseY, ingredient);
        ms.popPose();
        return hovered;
    }

    public static double nextXAngle(double angle, int radius) {
        return Math.cos(angle * Math.PI / 180D) * radius + 32;
    }

    public static double nextYAngle(double angle, int radius) {
        return Math.sin(angle * Math.PI / 180D) * radius + 32;
    }

    /**
     *
     * @return returns the hovered stack
     */
    public static ItemStack renderIngredient(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, Ingredient ingr) {
        ItemStack[] stacks = ingr.getItems();
        if (stacks.length > 0) {
            return DocClientUtils.renderItemStack(graphics, x, y,mouseX, mouseY, stacks[(ClientInfo.ticksInGame / 20) % stacks.length]);
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack renderItemStack(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        Font font = Minecraft.getInstance().font;
        graphics.renderItem(stack, x, y);
        graphics.renderItemDecorations(font, stack, x, y);
        if(GuiUtils.isMouseInRelativeRange(mouseX, mouseY, x, y, 16, 16)){
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public static void drawHeader(@Nullable Component title, GuiGraphics guiGraphics, int x, int y, int width, int mouseX, int mouseY, float partialTick) {
        DocClientUtils.blit(guiGraphics, DocAssets.UNDERLINE, x, y + 9);
        if(title != null) {
            GuiHelpers.drawCenteredStringNoShadow(Minecraft.getInstance().font, guiGraphics, title, x + width / 2, y, 0);
        }
    }

    public static void drawHeaderNoUnderline(@Nullable Component title, GuiGraphics guiGraphics, int x, int y, int width, int mouseX, int mouseY, float partialTick){
        if(title != null) {
            GuiHelpers.drawCenteredStringNoShadow(Minecraft.getInstance().font, guiGraphics, title, x + width / 2, y, 0);
        }
    }

    public static void drawParagraph(Component text, GuiGraphics guiGraphics, int x, int y, int width, int mouseX, int mouseY, float partialTick) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        float scale = 0.7f;
        poseStack.translate(x + 2, y, 0);
        poseStack.scale(scale, scale, 1);
        NuggetMultilLineLabel label = NuggetMultilLineLabel.create(Minecraft.getInstance().font, text, (int) (width * 1.48) - 6);
        label.renderLeftAlignedNoShadow(guiGraphics, 0, 0, 12, 0);

//        float dist = 0.08F;
//        for(int cycle = 0; cycle < 2; cycle++){
//            poseStack.translate(-dist, 0F, 0F);
//            label.renderLeftAlignedNoShadow(guiGraphics, 0, 0, 12, 0);
//            poseStack.translate(dist, -dist, 0F);
//            label.renderLeftAlignedNoShadow(guiGraphics, 0, 0, 12, 0);
//            poseStack.translate(dist, 0F, 0F);
//            label.renderLeftAlignedNoShadow(guiGraphics, 0, 0, 12, 0);
//            poseStack.translate(-dist, dist, 0F);
//
//            dist = -dist;
//        }
        poseStack.popPose();
    }
}
