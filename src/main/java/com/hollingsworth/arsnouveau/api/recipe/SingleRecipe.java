package com.hollingsworth.arsnouveau.api.recipe;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.*;

public class SingleRecipe {
    public List<Ingredient> recipeIngredients;
    public ItemStack outputStack;
    public Recipe iRecipe;


    public SingleRecipe(List<Ingredient> recipeIngredients, ItemStack outputStack, Recipe iRecipe) {
        this.recipeIngredients = recipeIngredients;
        this.outputStack = outputStack;
        this.iRecipe = iRecipe;
    }

    @Deprecated
    public List<ItemStack> canCraftFromInventory(Map<Item, Integer> inventory) {
        Map<Item, Integer> map = new HashMap<>(inventory);

        List<ItemStack> items = new ArrayList<>();
        for (Ingredient i : recipeIngredients) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingleRecipe recipe1 = (SingleRecipe) o;
        return Objects.equals(recipeIngredients, recipe1.recipeIngredients) &&
                Objects.equals(outputStack, recipe1.outputStack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipeIngredients, outputStack);
    }
}
