package com.hollingsworth.arsnouveau.api.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;

import java.util.*;

public class RecipeWrapper {

    public Set<SingleRecipe> recipes;


    public RecipeWrapper(){
        recipes = new HashSet<>();
    }

    public boolean addRecipe(SingleRecipe recipe){
        return recipes.add(recipe);
    }

    public boolean addRecipe(List<Ingredient> recipe, ItemStack outputStack, IRecipe iRecipe){
        return recipes.add(new SingleRecipe(recipe, outputStack, iRecipe));
    }

    public SingleRecipe canCraftFromInventory(Map<Item, Integer> inventory){
        for(SingleRecipe recipe: recipes){
            List<ItemStack> itemsNeeded = recipe.canCraftFromInventory(inventory);
            if(itemsNeeded != null )
                return recipe;
        }
        return null;
    }

    public static class SingleRecipe{
        public List<Ingredient> recipe;
        public ItemStack outputStack;
        public IRecipe iRecipe;


        public SingleRecipe(List<Ingredient> ingredients, ItemStack outputStack, IRecipe iRecipe){
            this.recipe = ingredients;
            this.outputStack = outputStack;
            this.iRecipe = iRecipe;
        }

        public List<ItemStack> canCraftFromInventory(Map<Item, Integer> inventory){
            Map<Item, Integer> map = new HashMap<>(inventory);

            List<ItemStack> items = new ArrayList<>();
            for(Ingredient i : recipe){
                boolean foundStack = false;
                for(ItemStack stack : i.getItems()){
                    // If our inventory has the item, decrease the effective count
                    if(inventory.containsKey(stack.getItem()) && map.get(stack.getItem()) > 0){
                        map.put(stack.getItem(), map.get(stack.getItem()) - 1);
                        foundStack = true;
                        items.add(stack.copy());
                        break;
                    }
                }
                if(!foundStack)
                    return null;
            }
            return items;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SingleRecipe recipe1 = (SingleRecipe) o;
            return Objects.equals(recipe, recipe1.recipe) &&
                    Objects.equals(outputStack, recipe1.outputStack);
        }

        @Override
        public int hashCode() {
            return Objects.hash(recipe, outputStack);
        }
    }
}
