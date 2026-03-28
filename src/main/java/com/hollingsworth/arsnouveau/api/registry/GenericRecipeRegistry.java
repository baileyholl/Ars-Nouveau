package com.hollingsworth.arsnouveau.api.registry;

import net.minecraft.world.item.crafting.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class GenericRecipeRegistry<C extends RecipeInput, T extends Recipe<C>> {
    private final Supplier<? extends RecipeType<? extends T>> type;

    public GenericRecipeRegistry(Supplier<? extends RecipeType<? extends T>> type) {
        this.type = type;
        REGISTRIES.add(this);
    }

    public List<RecipeHolder<? extends T>> RECIPES = new ArrayList<>();

    public List<RecipeHolder<? extends T>> getRecipes() {
        return Collections.unmodifiableList(RECIPES);
    }

    public RecipeType<? extends T> getType() {
        return type.get();
    }

    public void reload(RecipeManager manager) {
        RECIPES.clear();
        var recipes = manager.recipeMap().byType(type.get());
        RECIPES.addAll(recipes);
    }

    public static List<GenericRecipeRegistry<?, ?>> REGISTRIES = new ArrayList<>();

    public static void reloadAll(RecipeManager recipeManager) {
        for (GenericRecipeRegistry<?, ?> registry : REGISTRIES) {
            registry.reload(recipeManager);
        }
    }

    @SuppressWarnings("unchecked")
    public void reloadFromMap(RecipeMap recipeMap) {
        RECIPES.clear();
        Collection<RecipeHolder<? extends T>> recipes = (Collection<RecipeHolder<? extends T>>) (Collection<?>) recipeMap.byType(type.get());
        RECIPES.addAll(recipes);
    }

    public static void reloadAll(RecipeMap recipeMap) {
        for (GenericRecipeRegistry<?, ?> registry : REGISTRIES) {
            registry.reloadFromMap(recipeMap);
        }
    }
}
