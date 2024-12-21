package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class PedestalRecipeEntry extends SinglePageWidget{
    public List<Ingredient> ingredients;
    public ItemStack outputStack;
    public Ingredient reagentStack = Ingredient.EMPTY;
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
        for (Ingredient input : ingredients) {
            int renderX = x + 26;
            int renderY = y + 24;
            setTooltipIfHovered(DocClientUtils.renderIngredientAtAngle(guiGraphics, renderX, renderY, mouseX, mouseY, currentDegree, input));
            double itemX =  (renderX + DocClientUtils.nextXAngle(currentDegree - 90, 41));
            double itemY =  (renderY + DocClientUtils.nextYAngle(currentDegree - 90, 41));
            if(drawPedestals) {
                PoseStack poseStack = guiGraphics.pose();
                poseStack.pushPose();
                poseStack.translate(itemX - 3, itemY - 3, 0);
                DocClientUtils.blit(guiGraphics, DocAssets.PEDESTAL_FRAME, 0, 0);
                poseStack.popPose();
            }
            currentDegree += degreePerInput;
        }

        if(!reagentStack.isEmpty()){
            int itemX = x + width / 2 + 6;
            int itemY = y + 55;
            setTooltipIfHovered(DocClientUtils.renderIngredient(guiGraphics, itemX, itemY, mouseX, mouseY, reagentStack));
        }

        int itemX = x + width / 2 - 9;
        int itemY = y + 130;
        setTooltipIfHovered(DocClientUtils.renderItemStack(guiGraphics, itemX, itemY, mouseX, mouseY, outputStack));
    }
}
