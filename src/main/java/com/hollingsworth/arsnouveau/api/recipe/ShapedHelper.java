package com.hollingsworth.arsnouveau.api.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;

public class ShapedHelper {
    int recipeWidth;
    int recipeHeight;
    NonNullList<Ingredient> recipeItems;
    public List<List<Ingredient>> possibleRecipes;

    public ShapedHelper(ShapedRecipe recipe) {
        recipeHeight = recipe.getHeight();
        recipeWidth = recipe.getWidth();
        recipeItems = recipe.getIngredients();
        possibleRecipes = getPossibleRecipes();
    }


    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public List<List<Ingredient>> getPossibleRecipes() {
        List<List<Ingredient>> ingredients = new ArrayList<>();
        for (int i = 0; i <= 3 - this.recipeWidth; ++i) {
            for (int j = 0; j <= 3 - this.recipeHeight; ++j) {
                if (!this.checkMatch(i, j, true).isEmpty()) {
                    ingredients.add(this.checkMatch(i, j, true));
                }

                if (!this.checkMatch(i, j, false).isEmpty()) {
                    ingredients.add(this.checkMatch(i, j, false));
                }
            }
        }
        return ingredients;
    }

    /**
     * Checks if the region of a crafting inventory is match for the recipe.
     */
    private List<Ingredient> checkMatch(int width, int height, boolean p_77573_4_) {
        List<Ingredient> ingredientList = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                int k = i - width;
                int l = j - height;
                Ingredient ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < this.recipeWidth && l < this.recipeHeight) {
                    if (p_77573_4_) {
                        ingredient = this.recipeItems.get(this.recipeWidth - k - 1 + l * this.recipeWidth);
                    } else {
                        ingredient = this.recipeItems.get(k + l * this.recipeWidth);
                    }
                }
                if (ingredient.getItems().length != 0)
                    ingredientList.add(ingredient);
            }
        }

        return ingredientList;
    }

}
