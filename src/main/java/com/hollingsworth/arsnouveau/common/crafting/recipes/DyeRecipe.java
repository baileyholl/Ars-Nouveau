package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

public class DyeRecipe extends ShapelessRecipe {

    public DyeRecipe(String groupIn, CraftingBookCategory category, ItemStack recipeOutputIn, NonNullList<Ingredient> recipeItemsIn) {
        super(groupIn, CraftingBookCategory.MISC, recipeOutputIn, recipeItemsIn);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput inv, HolderLookup.@NotNull Provider p_266797_) {
        ItemStack output = super.assemble(inv, p_266797_);
        if (!output.isEmpty()) {
            // todo: 1.22 remove call to IDyeable
            for (int i = 0; i < inv.size(); i++) { // For each slot in the crafting inventory,
                final ItemStack ingredient = inv.getItem(i); // Get the ingredient in the slot
                if (!ingredient.isEmpty() && ingredient.getItem() instanceof IDyeable) {
                    output.applyComponents(ingredient.getComponentsPatch());
                }
            }
            for (int i = 0; i < inv.size(); i++) { // For each slot in the crafting inventory,
                final ItemStack ingredient = inv.getItem(i); // Get the ingredient in the slot
                DyeColor color = DyeColor.getColor(ingredient);
                if (!ingredient.isEmpty() && color != null) {
                    output.set(DataComponents.BASE_COLOR, color);
                }
            }
        }
        return output;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.DYE_RECIPE.get();
    }

}
