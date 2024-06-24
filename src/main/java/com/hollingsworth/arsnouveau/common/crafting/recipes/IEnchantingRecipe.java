package com.hollingsworth.arsnouveau.common.crafting.recipes;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface IEnchantingRecipe extends Recipe<ApparatusRecipeInput> {

    @Override
    default boolean matches(ApparatusRecipeInput input, Level level) {
        return matches(input, level, null);
    }

    boolean matches(ApparatusRecipeInput input, Level level, @Nullable Player player);

    default boolean consumesSource() {
        return sourceCost() > 0;
    }

    int sourceCost();

    @Override
    default boolean isSpecial() {
        return true;
    }

    @Override
    default boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }
}
