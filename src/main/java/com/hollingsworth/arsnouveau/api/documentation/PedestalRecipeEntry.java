package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.GuiUtils;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class PedestalRecipeEntry extends SinglePageWidget{
    public List<Ingredient> ingredients;
    public ItemStack outputStack;
    public Ingredient reagentStack = Ingredient.EMPTY;
    ItemStack tooltipStack = ItemStack.EMPTY;
    public Component title;
    public DocAssets.BlitInfo image;
    public boolean spinning = false;
    public boolean drawPedestals = true;

    public PedestalRecipeEntry(BaseDocScreen parent, int x, int y, int width, int height, List<Ingredient> ingredients, ItemStack outputStack, Component title, DocAssets.BlitInfo image) {
        super(parent, x, y, width, height);
        this.ingredients = ingredients;
        this.outputStack = outputStack;
        this.title = title;
        this.image = image;
    }

    public PedestalRecipeEntry(BaseDocScreen parent, int x, int y, int width, int height){
        super(parent, x, y, width, height);
        ingredients = new ArrayList<>();
        outputStack = ItemStack.EMPTY;
        title = Component.empty();
        image = DocAssets.APPARATUS_RECIPE;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        drawHeader(title, guiGraphics, mouseX,mouseY, partialTick);
        DocClientUtils.blit(guiGraphics, image,x + 20, y + 22);

        int degreePerInput = (int) (360F / ingredients.size());
        float currentDegree = spinning ? ClientInfo.ticksInGame + partialTick : 0;
        this.tooltipStack = ItemStack.EMPTY;
        for (Ingredient input : ingredients) {
            int renderX = x + 26;
            int renderY = y + 24;
            DocClientUtils.renderIngredientAtAngle(guiGraphics, renderX, renderY, currentDegree, input);
            double itemX =  (renderX + DocClientUtils.nextXAngle(currentDegree - 90, 41));
            double itemY =  (renderY + DocClientUtils.nextYAngle(currentDegree - 90, 41));
            if(drawPedestals) {
                PoseStack poseStack = guiGraphics.pose();
                poseStack.pushPose();
                poseStack.translate(itemX - 3, itemY - 3, 0);
                DocClientUtils.blit(guiGraphics, DocAssets.PEDESTAL_FRAME, 0, 0);
                poseStack.popPose();
            }
            if (GuiUtils.isMouseInRelativeRange(mouseX, mouseY, (int) itemX, (int) itemY, 16, 16)) {
                this.tooltipStack = input.getItems()[ClientInfo.ticksInGame / 20 % input.getItems().length];
            }
            currentDegree += degreePerInput;
        }

        if(!reagentStack.isEmpty()){
            int itemX = x + width / 2 + 6;
            int itemY = y + 55;
            ItemStack stack = reagentStack.getItems()[ClientInfo.ticksInGame / 20 % reagentStack.getItems().length];
            RenderUtils.drawItemAsIcon(stack, guiGraphics, itemX, itemY, 16, false);
            if (GuiUtils.isMouseInRelativeRange(mouseX, mouseY, itemX, itemY, 16, 16)) {
                this.tooltipStack = stack;
            }
        }

        int itemX = x + width / 2 - 9;
        int itemY = y + 130;
        RenderUtils.drawItemAsIcon(outputStack, guiGraphics,  itemX, itemY, 16, false);
        if (GuiUtils.isMouseInRelativeRange(mouseX, mouseY, itemX, itemY, 16, 16)) {
            this.tooltipStack = outputStack;
        }
    }

    @Override
    public void gatherTooltips(List<Component> list) {
        super.gatherTooltips(list);
        if (!tooltipStack.isEmpty()) {
            list.addAll(tooltipStack.getTooltipLines(Item.TooltipContext.EMPTY, null, TooltipFlag.NORMAL));
        }
    }
}
