package com.hollingsworth.arsnouveau.api.recipe;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Stores a list of recipes and provides methods to check if a given inventory contains the items needed to craft any of the recipes.
 * These recipes typically all correspond to the same output item.
 */
public class MultiRecipeWrapper implements IRecipeWrapper {

    public Set<SingleRecipe> recipes;

    public static Map<Item, MultiRecipeWrapper> RECIPE_CACHE = new HashMap<>();

    public MultiRecipeWrapper() {
        recipes = new HashSet<>();
    }

    public static MultiRecipeWrapper fromStack(ItemStack stack, Level level) {
        MultiRecipeWrapper wrapper = new MultiRecipeWrapper();
        if (stack.getItem() == Items.POTION) {
            for (BrewingRecipe r : ArsNouveauAPI.getInstance().getAllPotionRecipes(level)) {
                if (ItemStack.matches(stack, r.getOutput())) {
                    List<Ingredient> list = new ArrayList<>();
                    // getInput() returns an Ingredient; get first item holder to build PotionIngredient
                    r.getInput().items().findFirst().ifPresent(h -> list.add(PotionIngredient.getIngredient(new ItemStack(h.value()))));
                    list.add(r.getIngredient());
                    wrapper.addRecipe(list, r.getOutput(), null);
                }
            }
        } else {
            if (RECIPE_CACHE.containsKey(stack.getItem())) {
                return RECIPE_CACHE.get(stack.getItem());
            }
            for (RecipeHolder<?> rh : level.getServer().getRecipeManager().getRecipes()) {
                Recipe<?> r = rh.value();
                // Get result via assemble with empty container - use display() for item check
                ItemStack result = getRecipeResult(r, level);
                if (result == null || result.isEmpty() || result.getItem() != stack.getItem())
                    continue;

                if (r instanceof ShapedRecipe shaped) {
                    ShapedHelper helper = new ShapedHelper(shaped);
                    for (List<Ingredient> iList : helper.getPossibleRecipes()) {
                        wrapper.addRecipe(iList, result, r);
                    }
                }

                if (r instanceof ShapelessRecipe shapeless)
                    wrapper.addRecipe(shapeless.placementInfo().ingredients(), result, r);

            }
            RECIPE_CACHE.put(stack.getItem(), wrapper);
        }
        return wrapper;
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
    public List<ItemStack> getItemsNeeded(Map<Item, Integer> inventory, Level world, BlockPos pos, SingleRecipe recipe) {
        Map<Item, Integer> map = new HashMap<>(inventory);

        List<ItemStack> items = new ArrayList<>();
        for (Ingredient i : recipe.recipeIngredients) {
            boolean foundStack = false;
            for (Holder<Item> holder : i.items().toList()) {
                Item itemType = holder.value();
                ItemStack stack = new ItemStack(itemType);
                // Return success if we could consume this potion as a liquid from a jar
                if (itemType == Items.POTION) {
                    PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);
                    if (potionContents == null) continue;

                    if (potionContents.is(Potions.WATER) || WixieCauldronTile.findNeededPotion(potionContents, 300, world, pos) != null) {
                        foundStack = true;
                        break;
                    }
                }
                // If our inventory has the item, decrease the effective count
                if (inventory.containsKey(itemType) && map.get(itemType) > 0) {
                    map.put(itemType, map.get(itemType) - 1);
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

    /** Get result ItemStack from a recipe, using display() API since getResultItem() was removed in 1.21.11 */
    @Nullable
    private static ItemStack getRecipeResult(Recipe<?> recipe, Level level) {
        try {
            var displays = recipe.display();
            if (!displays.isEmpty()) {
                var slotDisplay = displays.get(0).result();
                if (slotDisplay instanceof net.minecraft.world.item.crafting.display.SlotDisplay.ItemStackSlotDisplay itemStackDisplay) {
                    return itemStackDisplay.stack();
                }
                if (slotDisplay instanceof net.minecraft.world.item.crafting.display.SlotDisplay.ItemSlotDisplay itemDisplay) {
                    return new ItemStack(itemDisplay.item());
                }
            }
        } catch (Exception ignored) {}
        return null;
    }


    public boolean addRecipe(List<Ingredient> recipe, ItemStack outputStack, Recipe iRecipe) {
        return recipes.add(new SingleRecipe(recipe, outputStack, iRecipe));
    }

    public boolean isEmpty() {
        return recipes.isEmpty();
    }
}
