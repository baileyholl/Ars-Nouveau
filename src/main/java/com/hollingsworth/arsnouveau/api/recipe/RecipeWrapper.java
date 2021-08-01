package com.hollingsworth.arsnouveau.api.recipe;

import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

    public SingleRecipe canCraftPotionFromInventory(Map<Item, Integer> inventory, World world, BlockPos pos){
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
        public IRecipe iRecipe;


        public SingleRecipe(List<Ingredient> ingredients, ItemStack outputStack, IRecipe iRecipe){
            this.recipe = ingredients;
            this.outputStack = outputStack;
            this.iRecipe = iRecipe;
        }

        public List<ItemStack> canCraftPotionFromInventory(Map<Item, Integer> inventory, World world, BlockPos pos){
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
