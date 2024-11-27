package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

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
        if(recipe1 == null && recipe2 == null){
            return;
        }
        if(recipe2 == null){
            DocClientUtils.blit(guiGraphics, DocAssets.CRAFTING_ENTRY_1, x, y);
        }
    }
}
