package com.hollingsworth.arsnouveau.api.documentation.entry;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageWidget;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;

public class PotionRecipeEntry extends SinglePageWidget {

    BrewingRecipe recipe1;
    BrewingRecipe recipe2;

    public PotionRecipeEntry(BaseDocScreen parent, int x, int y, int width, int height, BrewingRecipe recipe, BrewingRecipe recipe2) {
        super(parent, x, y, width, height);
        this.recipe1 = recipe;
        this.recipe2 = recipe2;
    }

    public static SinglePageCtor create(BrewingRecipe recipe, BrewingRecipe recipe2){
        return (parent, x, y, width, height) -> new PotionRecipeEntry(parent, x, y, width, height, recipe, recipe2);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        DocClientUtils.drawHeader(Component.translatable("block.minecraft.brewing_stand"), guiGraphics, x, y, width, mouseX, mouseY, partialTick);
        if(recipe1 != null) {
            drawBrewingRecipe(guiGraphics, x + 2, y + 20, mouseX, mouseY, recipe1);
        }

        if(recipe2 != null) {
            drawBrewingRecipe(guiGraphics, x + 2, y + 88, mouseX, mouseY, recipe2);
        }
    }


    public void drawBrewingRecipe(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, BrewingRecipe recipe){
        DocClientUtils.blit(guiGraphics, DocAssets.POTION_RECIPE, x, y);
        Ingredient ingredient = recipe.getInput();
        for(int i = 0; i < 3; i++){
            int col = i % 3;
            int renderX = x  + 4 + (col * 22);
            int renderY = y + 35;
            this.setTooltipIfHovered(DocClientUtils.renderIngredient(guiGraphics, renderX, renderY, mouseX, mouseY, ingredient));
        }
        this.setTooltipIfHovered(DocClientUtils.renderIngredient(guiGraphics, x + 26, y + 2, mouseX, mouseY, recipe.getIngredient()));
        this.setTooltipIfHovered(DocClientUtils.renderItemStack(guiGraphics, x + 93, y + 22, mouseX, mouseY, recipe.getOutput()));
    }

}
