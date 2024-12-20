package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class CraftingEntry extends SinglePageWidget{

    public RecipeHolder<CraftingRecipe> recipe1;
    public RecipeHolder<CraftingRecipe> recipe2;

    public CraftingEntry(RecipeHolder<CraftingRecipe> recipe1, RecipeHolder<CraftingRecipe> recipe2, BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        this.recipe1 = recipe1;
        this.recipe2 = recipe2;
    }

    public CraftingEntry(RecipeHolder<CraftingRecipe> recipe1, BaseDocScreen parent, int x, int y, int width, int height) {
        this(recipe1, null, parent, x, y, width, height);
    }


    public static SinglePageCtor create(RecipeHolder<CraftingRecipe> recipe1){
        return (parent, x, y, width, height) -> new CraftingEntry(recipe1, parent, x, y, width, height);
    }

    public static SinglePageCtor create(RecipeHolder<CraftingRecipe> recipe1, RecipeHolder<CraftingRecipe> recipe2){
        return (parent, x, y, width, height) -> new CraftingEntry(recipe1, recipe2, parent, x, y, width, height);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        drawHeader(Component.translatable("block.minecraft.crafting_table"), guiGraphics, mouseX, mouseY, partialTick);
        if(recipe1 == null && recipe2 == null){
            return;
        }
        if(recipe1 != null) {
            drawCraftingGrid(guiGraphics, x + 14, y + 16, mouseX, mouseY, recipe1);
        }

        if(recipe2 != null) {
            drawCraftingGrid(guiGraphics, x + 14, y + 84, mouseX, mouseY, recipe2);
        }
    }

    public void drawCraftingGrid(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, RecipeHolder<CraftingRecipe> recipe){
        ItemStack outputStack = recipe.value().getResultItem(Minecraft.getInstance().level.registryAccess());
        List<Ingredient> ingredients = recipe.value().getIngredients();
        DocClientUtils.blit(guiGraphics, DocAssets.CRAFTING_ENTRY_1, x, y);
        int row = 0;
        for(int i = 0; i < ingredients.size(); i++){
            Ingredient ingredient = ingredients.get(i);
            int col = i % 3;
            if(col == 0 && i != 0){
                row++;
            }
            int renderX = x + 3 + (col * 21);
            int renderY = y + 3 + (row * 21);
            this.setTooltipIfHovered(DocClientUtils.renderIngredient(guiGraphics, renderX, renderY, mouseX, mouseY, ingredient));
        }

        this.setTooltipIfHovered(DocClientUtils.renderItemStack(guiGraphics, x + 86, y + 24, mouseX, mouseY, outputStack));
    }
}