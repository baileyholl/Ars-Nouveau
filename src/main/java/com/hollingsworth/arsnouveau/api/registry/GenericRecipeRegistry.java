package com.hollingsworth.arsnouveau.api.registry;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenericRecipeRegistry<C extends Container, T extends Recipe<C>> {
    private final RegistryObject<? extends RecipeType<? extends T>> type;

    public GenericRecipeRegistry(RegistryObject<? extends RecipeType<? extends T>> type) {
        this.type = type;
        REGISTRIES.add(this);
    }

    public List<T> RECIPES = new ArrayList<>();

    public List<T> getRecipes() {
        return Collections.unmodifiableList(RECIPES);
    }

    public RecipeType<? extends T> getType() {
        return type.get();
    }

    public void reload(RecipeManager manager) {
        RECIPES.clear();
        List<? extends T> recipes = manager.getAllRecipesFor(type.get());
        RECIPES.addAll(recipes);
    }

    public static List<GenericRecipeRegistry<?, ?>> REGISTRIES = new ArrayList<>();

    public static void reloadAll(RecipeManager recipeManager) {
        for (GenericRecipeRegistry<?, ?> registry : REGISTRIES) {
            registry.reload(recipeManager);
        }
    }
}
