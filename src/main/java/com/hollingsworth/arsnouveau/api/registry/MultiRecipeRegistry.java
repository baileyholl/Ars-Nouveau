package com.hollingsworth.arsnouveau.api.registry;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class MultiRecipeRegistry<C extends Container, T extends Recipe<C>> {
    private final List<GenericRecipeRegistry<C, T>> recipeRegistries = new ArrayList();

    public <R extends T> void addRecipeType(RegistryObject<? extends RecipeType<R>> recipeType) {
        recipeRegistries.add(new GenericRecipeRegistry<>(recipeType));
    }

    public List<RecipeType<? extends T>> getRecipeTypes() {
        ImmutableList.Builder<RecipeType<? extends T>> builder = ImmutableList.builder();
        for (GenericRecipeRegistry<C, T> registry : recipeRegistries) {
            builder.add(registry.getType());
        }
        return builder.build();
    }

    public List<T> getRecipes() {
        ImmutableList.Builder<T> builder = ImmutableList.builder();
        for (GenericRecipeRegistry<C, T> registry : recipeRegistries) {
            builder.addAll(registry.getRecipes());
        }
        return builder.build();
    }
}
