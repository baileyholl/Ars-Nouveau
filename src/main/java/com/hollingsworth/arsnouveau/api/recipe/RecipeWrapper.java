package com.hollingsworth.arsnouveau.api.recipe;

import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.*;

public class RecipeWrapper {

    public Set<SingleRecipe> recipes;


    public RecipeWrapper(){
        recipes = new HashSet<>();
    }

    public boolean addRecipe(SingleRecipe recipe){
        return recipes.add(recipe);
    }

    public boolean addRecipe(List<Ingredient> recipe, ItemStack outputStack, Recipe iRecipe){
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

    public SingleRecipe canCraftPotionFromInventory(Map<Item, Integer> inventory, Level world, BlockPos pos){
        for(SingleRecipe recipe: recipes){
            List<ItemStack> itemsNeeded = recipe.canCraftPotionFromInventory(inventory, world, pos);
            if(itemsNeeded != null )
                return recipe;
        }
        return null;
    }

    public static class SingleRecipe{
        public List<Ingredient> recipe;
        public ItemStack outputStack;
        public Recipe iRecipe;


        public SingleRecipe(List<Ingredient> ingredients, ItemStack outputStack, Recipe iRecipe){
            this.recipe = ingredients;
            this.outputStack = outputStack;
            this.iRecipe = iRecipe;
        }

        public List<ItemStack> canCraftPotionFromInventory(Map<Item, Integer> inventory, Level world, BlockPos pos){
            Map<Item, Integer> map = new HashMap<>(inventory);

            List<ItemStack> items = new ArrayList<>();
            for(Ingredient i : recipe){
                boolean foundStack = false;
                for(ItemStack stack : i.getItems()){
                    // Return success if we could consume this potion as a liquid from a jar
                    if(stack.getItem() == Items.POTION){
                        Potion potion = PotionUtils.getPotion(stack);
                        if(potion == Potions.WATER || WixieCauldronTile.findNeededPotion(PotionUtils.getPotion(stack), 300, world, pos) != null) {
                            foundStack = true;
                        }else{
                            return null;
                        }
                        continue;
                    }
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
