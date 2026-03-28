package com.hollingsworth.arsnouveau.api.recipe;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.*;

public class SingleRecipe {
    public List<Ingredient> recipeIngredients;
    public ItemStack outputStack;
    public Recipe<?> iRecipe;


    public SingleRecipe(List<Ingredient> recipeIngredients, ItemStack outputStack, Recipe<?> iRecipe) {
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
            for (Holder<Item> holder : i.items().toList()) {
                Item item = holder.value();
                ItemStack stack = new ItemStack(item);
                // If our inventory has the item, decrease the effective count
                if (inventory.containsKey(item) && map.get(item) > 0) {
                    map.put(item, map.get(item) - 1);
                    foundStack = true;
                    items.add(stack);
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
