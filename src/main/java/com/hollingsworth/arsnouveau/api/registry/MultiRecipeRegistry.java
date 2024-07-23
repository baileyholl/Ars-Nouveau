package com.hollingsworth.arsnouveau.api.registry;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MultiRecipeRegistry<C extends RecipeInput, T extends Recipe<C>> {
    private final List<GenericRecipeRegistry<C, T>> recipeRegistries = new ArrayList();

    public <R extends T> void addRecipeType(Supplier<? extends RecipeType<R>> recipeType) {
        recipeRegistries.add(new GenericRecipeRegistry<>(recipeType));
    }

    public List<RecipeType<? extends T>> getRecipeTypes() {
        ImmutableList.Builder<RecipeType<? extends T>> builder = ImmutableList.builder();
        for (GenericRecipeRegistry<C, T> registry : recipeRegistries) {
            builder.add(registry.getType());
        }
        return builder.build();
    }

    public List<RecipeHolder<? extends T>> getRecipes() {
        ImmutableList.Builder<RecipeHolder<? extends T>> builder = ImmutableList.builder();
        for (GenericRecipeRegistry<C, T> registry : recipeRegistries) {
            builder.addAll(registry.getRecipes());
        }
        return builder.build();
    }
}
