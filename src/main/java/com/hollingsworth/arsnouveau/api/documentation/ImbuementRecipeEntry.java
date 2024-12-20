package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ImbuementRecipeEntry extends PedestalRecipeEntry{
    RecipeHolder<ImbuementRecipe> recipe;

    public ImbuementRecipeEntry(RecipeHolder<ImbuementRecipe> recipe, BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        this.recipe = recipe;
        this.image = DocAssets.IMBUEMENT_RECIPE;
        this.title = Component.translatable("block.ars_nouveau.imbuement_chamber");
        if(recipe != null && recipe.value() != null) {
            this.outputStack = recipe.value().output;
            this.ingredients = recipe.value().getPedestalItems();
            this.reagentStack = recipe.value().input;
        }
    }

    public static SinglePageCtor create(RecipeHolder<ImbuementRecipe> recipe){
        return (parent, x, y, width, height) -> new ImbuementRecipeEntry(recipe, parent, x, y, width, height);
    }
}