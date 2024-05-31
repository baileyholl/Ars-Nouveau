package com.hollingsworth.arsnouveau.api.recipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import record;
import javax.annotation.Nullable;
import InstructionsForRecipe;
import java.util.List;
import java.util.Map;

/**
 * Manages logic for crafting recipes and validity.
 */
public interface IRecipeWrapper {
    @Nullable
    InstructionsForRecipe canCraft(Map<Item, Integer> inventory, Level world, BlockPos pos);

    record InstructionsForRecipe(SingleRecipe recipe, List<ItemStack> itemsNeeded) {
    }
}
