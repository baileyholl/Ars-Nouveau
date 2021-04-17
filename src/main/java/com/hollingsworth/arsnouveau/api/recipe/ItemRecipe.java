package com.hollingsworth.arsnouveau.api.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A wrapper for ingredient lists.
 */
public class ItemRecipe {
    List<Ingredient> ingredientList;

    public ItemRecipe(List<Ingredient> ingredients){
        ingredientList = ingredients;
    }

    /**
     *
     */
    public List<ItemStack> canRecipeBeMade(Map<Item, Integer> inventoryCount){
        Map<Item, Integer> invCopy = new HashMap<>(inventoryCount);
        List<ItemStack> finalItems = new ArrayList<>();
        for(Ingredient i : ingredientList){
            ItemStack stack = inventoryContainsItem(invCopy, i.getItems());
            if(!stack.isEmpty()){
                finalItems.add(stack);
                invCopy.put(stack.getItem(), invCopy.get(stack.getItem()) - 1);
            }else{
                return new ArrayList<>();
            }
        }
        return finalItems;
    }

    private ItemStack inventoryContainsItem(Map<Item, Integer> inventoryCount, ItemStack[] possibleItems){
        if(possibleItems == null || inventoryCount == null)
            return ItemStack.EMPTY;

        for (ItemStack i : possibleItems) {
            if (inventoryCount.containsKey(i.getItem()) && inventoryCount.get(i.getItem()) > 0) {
                return i;
            }
        }
        return ItemStack.EMPTY;
    }
}
