package com.hollingsworth.arsnouveau.common.potions;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraftforge.common.brewing.IBrewingRecipe;

import javax.annotation.Nonnull;

public class SimpleBrewingRecipe implements IBrewingRecipe {

    @Nonnull private final Ingredient ingredient;
    @Nonnull private final ItemStack output;

    public SimpleBrewingRecipe(Ingredient ingredient, ItemStack output)
    {
        this.ingredient = ingredient;
        this.output = output;
    }
    @Override
    public boolean isInput(ItemStack input) {
        return input.getItem() == Items.POTION && PotionUtils.getPotionFromItem(input) == Potions.AWKWARD;
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient)
    {
        return isInput(input) && isIngredient(ingredient) ? getOutput().copy() : ItemStack.EMPTY;
    }

    public Ingredient getIngredient()
    {
        return ingredient;
    }

    public ItemStack getOutput()
    {
        return output;
    }

    @Override
    public boolean isIngredient(ItemStack ingredient)
    {
        return this.ingredient.test(ingredient);
    }
}
