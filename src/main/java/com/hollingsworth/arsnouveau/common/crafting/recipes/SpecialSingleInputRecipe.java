package com.hollingsworth.arsnouveau.common.crafting.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public interface SpecialSingleInputRecipe extends Recipe<SingleRecipeInput> {

    default boolean matches(ItemStack stack, Level level) {
        return this.matches(new SingleRecipeInput(stack), level);
    }

    @Override
    default ItemStack assemble(SingleRecipeInput p_345149_, HolderLookup.Provider p_346030_) {
        return ItemStack.EMPTY;
    }

    @Override
    default PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    default RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    default boolean isSpecial() {
        return true;
    }
}
