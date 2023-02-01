package com.hollingsworth.arsnouveau.api.recipe;

import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PotionRecipeWrapper extends MultiRecipeWrapper{

    public PotionRecipeWrapper() {}

    public List<ItemStack> getItemsNeeded(Map<Item, Integer> inventory, Level world, BlockPos pos, SingleRecipe recipe) {
        Map<Item, Integer> map = new HashMap<>(inventory);
        List<ItemStack> items = new ArrayList<>();
        for (Ingredient i : recipe.recipeIngredients) {
            boolean foundStack = false;
            for (ItemStack stack : i.getItems()) {
                // Return success if we could consume this potion as a liquid from a jar
                if (stack.getItem() == Items.POTION) {
                    Potion potion = PotionUtils.getPotion(stack);
                    if (potion == Potions.WATER || WixieCauldronTile.findNeededPotion(potion, 300, world, pos) != null) {
                        foundStack = true;
                        break;
                    }
                }
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
}
