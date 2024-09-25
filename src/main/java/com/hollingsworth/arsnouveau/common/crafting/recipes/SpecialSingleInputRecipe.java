package com.hollingsworth.arsnouveau.common.crafting.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public interface SpecialSingleInputRecipe extends Recipe<SingleRecipeInput> {

    default boolean matches(ItemStack stack, Level level){
        return this.matches(new SingleRecipeInput(stack), level);
    }

    @Override
    default ItemStack assemble(SingleRecipeInput p_345149_, HolderLookup.Provider p_346030_) {
        return ItemStack.EMPTY;
    }

    @Override
    default boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    default ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return ItemStack.EMPTY;
    }

    @Override
    default boolean isSpecial() {
        return true;
    }
}
