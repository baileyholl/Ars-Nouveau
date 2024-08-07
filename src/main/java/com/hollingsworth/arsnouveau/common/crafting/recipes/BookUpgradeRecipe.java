package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

public class BookUpgradeRecipe extends ShapelessRecipe {

    public BookUpgradeRecipe(final String group, CraftingBookCategory category, final ItemStack recipeOutput, final NonNullList<Ingredient> ingredients) {
        super(group, CraftingBookCategory.MISC, recipeOutput, ingredients);
    }

    @Override
    public @NotNull ItemStack assemble(final @NotNull CraftingInput inv, HolderLookup.@NotNull Provider p_266797_) {
        final ItemStack output = super.assemble(inv, p_266797_); // Get the default output

        if (!output.isEmpty()) {
            for (int i = 0; i < inv.size(); i++) { // For each slot in the crafting inventory,
                final ItemStack ingredient = inv.getItem(i); // Get the ingredient in the slot
                if (!ingredient.isEmpty() && ingredient.getItem() instanceof SpellBook) {
                    output.applyComponents(ingredient.getComponentsPatch());
                }
            }
        }

        return output; // Return the modified output
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.BOOK_UPGRADE_RECIPE.get();
    }
}
