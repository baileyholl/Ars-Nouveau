package com.hollingsworth.arsnouveau.api.recipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MultiRecipeWrapper implements IRecipeWrapper{

    public Set<SingleRecipe> recipes;

    public MultiRecipeWrapper() {
        recipes = new HashSet<>();
    }

    @Nullable
    @Override
    public InstructionsForRecipe canCraft(Map<Item, Integer> inventory, Level world, BlockPos pos) {
        for (SingleRecipe recipe : recipes) {
            List<ItemStack> itemsNeeded = getItemsNeeded(inventory, world, pos, recipe);
            if (itemsNeeded != null) {
                return new InstructionsForRecipe(recipe, itemsNeeded);
            }
        }
        return null;
    }

    /**
     * Returns a list of items needed to craft the recipe, or null if the recipe can't be crafted.
     * This method tracks that the inventory has enough of each item to craft the recipe.
     */
    @Nullable
    public List<ItemStack> getItemsNeeded(Map<Item, Integer> inventory, Level world, BlockPos pos, SingleRecipe recipe){
        Map<Item, Integer> map = new HashMap<>(inventory);

        List<ItemStack> items = new ArrayList<>();
        for (Ingredient i : recipe.recipeIngredients) {
            boolean foundStack = false;
            for (ItemStack stack : i.getItems()) {
                // If our inventory has the item, decrease the effective count
                if (inventory.containsKey(stack.getItem()) && map.get(stack.getItem()) > 0) {
                    map.put(stack.getItem(), map.get(stack.getItem()) - 1);
                    foundStack = true;
                    items.add(stack.copy());
                    break;
                }
            }
            if (!foundStack)
                return null;
        }
        return items;
    }


    public boolean addRecipe(List<Ingredient> recipe, ItemStack outputStack, Recipe iRecipe) {
        return recipes.add(new SingleRecipe(recipe, outputStack, iRecipe));
    }

    public boolean isEmpty(){
        return recipes.isEmpty();
    }
}
