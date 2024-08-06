package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.recipe.BuddingConversionRecipe;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuddingConversionRegistry {

    private static List<BuddingConversionRecipe> RECIPES = new ArrayList<>();

    public static List<BuddingConversionRecipe> getRecipes(){
        return Collections.unmodifiableList(RECIPES);
    }

    public static void reloadBuddingConversionRecipes(RecipeManager recipeManager){
        RECIPES = new ArrayList<>();
        List<BuddingConversionRecipe> recipes = recipeManager.getAllRecipesFor(RecipeRegistry.BUDDING_CONVERSION_TYPE.get());
        RECIPES.addAll(recipes);
    }
}
