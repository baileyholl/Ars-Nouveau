package com.hollingsworth.arsnouveau.common.crafting.recipes;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.crafting.CraftingHelper;

/**
 * https://github.com/Choonster-Minecraft-Mods/TestMod3/blob/1.14.4/src/main/java/choonster/testmod3/crafting/recipe/RecipeUtil.java
 * @author Choonster
 */
public class RecipeUtil {


    /**
     * Parse the input of a shapeless recipe.
     *
     * @param json The recipe's JSON object
     * @return A NonNullList containing the ingredients specified in the JSON object
     */
    public static NonNullList<Ingredient> parseShapeless(final JsonObject json) {
        final NonNullList<Ingredient> ingredients = NonNullList.create();
        for (final JsonElement element : JSONUtils.getAsJsonArray(json, "ingredients"))
            ingredients.add(CraftingHelper.getIngredient(element));

        if (ingredients.isEmpty())
            throw new JsonParseException("No ingredients for shapeless recipe");

        return ingredients;
    }

    public static class ShapedPrimer {
        private final NonNullList<Ingredient> ingredients;
        private final int recipeWidth;
        private final int recipeHeight;

        public ShapedPrimer(final NonNullList<Ingredient> ingredients, final int recipeWidth, final int recipeHeight) {
            this.ingredients = ingredients;
            this.recipeWidth = recipeWidth;
            this.recipeHeight = recipeHeight;
        }

        public NonNullList<Ingredient> getIngredients() {
            return ingredients;
        }

        public int getRecipeWidth() {
            return recipeWidth;
        }

        public int getRecipeHeight() {
            return recipeHeight;
        }
    }
}