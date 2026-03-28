package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.common.crafting.recipes.ScryRitualRecipe;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScryRitualRegistry {
    private static List<ScryRitualRecipe> RECIPES = new ArrayList<>();

    public static List<ScryRitualRecipe> getRecipes() {
        return Collections.unmodifiableList(RECIPES);
    }

    public static void reloadScryRitualRecipes(RecipeManager recipeManager) {
        RECIPES = new ArrayList<>();
        // 1.21.11: recipeMap().byType() returns Collection<RecipeHolder<T>>, not List
        var recipes = recipeManager.recipeMap().byType(RecipeRegistry.SCRY_RITUAL_TYPE.get());
        RECIPES.addAll(recipes.stream().map(RecipeHolder::value).toList());
    }
}
