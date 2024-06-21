package com.hollingsworth.arsnouveau.common.crafting.recipes;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.CraftingHelper;

/**
 * https://github.com/Choonster-Minecraft-Mods/TestMod3/blob/1.14.4/src/main/java/choonster/testmod3/crafting/recipe/RecipeUtil.java
 *
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
        for (final JsonElement element : GsonHelper.getAsJsonArray(json, "ingredients"))
            ingredients.add(CraftingHelper.getIngredient(element, true));

        if (ingredients.isEmpty())
            throw new JsonParseException("No ingredients for shapeless recipe");

        return ingredients;
    }

}