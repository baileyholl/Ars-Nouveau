package com.hollingsworth.arsnouveau.api.documentation.entry;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageWidget;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import org.joml.Matrix3x2fStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class PedestalRecipeEntry extends SinglePageWidget {
    public List<Ingredient> ingredients;
    public ItemStack outputStack;
    public Ingredient reagentStack = null; // Ingredient.EMPTY removed in MC 1.21.11; null means no reagent
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

    public PedestalRecipeEntry(BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        ingredients = new ArrayList<>();
        outputStack = ItemStack.EMPTY;
        title = Component.empty();
        image = DocAssets.APPARATUS_RECIPE;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        DocClientUtils.drawHeader(title, guiGraphics, x, y, width, mouseX, mouseY, partialTick);
        int yOffset = 24;
        DocClientUtils.blit(guiGraphics, image, x + 13, y + yOffset);

        int degreePerInput = (int) (360F / ingredients.size());
        float currentDegree = spinning ? ClientInfo.ticksInGame + partialTick : 0;
        for (Ingredient input : ingredients) {
            int renderX = x + 19;
            int renderY = y + yOffset + 2;
            setTooltipIfHovered(DocClientUtils.renderIngredientAtAngle(guiGraphics, renderX, renderY, mouseX, mouseY, currentDegree, input));
            double itemX = (renderX + DocClientUtils.nextXAngle(currentDegree - 90, 41));
            double itemY = (renderY + DocClientUtils.nextYAngle(currentDegree - 90, 41));
            if (drawPedestals) {
                // 1.21.11: GuiGraphics.pose() returns Matrix3x2fStack; use pushMatrix/popMatrix and translate(x,y)
                Matrix3x2fStack poseStack = guiGraphics.pose();
                poseStack.pushMatrix();
                poseStack.translate((float)(itemX - 3), (float)(itemY - 3));
                DocClientUtils.blit(guiGraphics, DocAssets.PEDESTAL_FRAME, 0, 0);
                poseStack.popMatrix();
            }
            currentDegree += degreePerInput;
        }

        if (reagentStack != null && !reagentStack.isEmpty()) {
            int itemX = x + width / 2 + 7;
            int itemY = y + yOffset + 33;
            setTooltipIfHovered(DocClientUtils.renderIngredient(guiGraphics, itemX, itemY, mouseX, mouseY, reagentStack));
        }

        int itemX = x + width / 2 - 8;
        int itemY = y + yOffset + 108;
        setTooltipIfHovered(DocClientUtils.renderItemStack(guiGraphics, itemX, itemY, mouseX, mouseY, outputStack));
    }
}
