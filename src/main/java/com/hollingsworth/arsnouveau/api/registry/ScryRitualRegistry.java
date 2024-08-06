package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.recipe.ScryRitualRecipe;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScryRitualRegistry {
    private static List<ScryRitualRecipe> RECIPES = new ArrayList<>();

    public static List<ScryRitualRecipe> getRecipes(){
        return Collections.unmodifiableList(RECIPES);
    }

    public static void reloadScryRitualRecipes(RecipeManager recipeManager){
        RECIPES = new ArrayList<>();
        List<ScryRitualRecipe> recipes = recipeManager.getAllRecipesFor(RecipeRegistry.SCRY_RITUAL_TYPE.get());
        RECIPES.addAll(recipes);
    }
}
