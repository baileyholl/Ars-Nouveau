package com.hollingsworth.arsnouveau.api.enchanting_apparatus;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface IEnchantingRecipe {

    boolean isMatch(List<ItemStack> pedestalItems, ItemStack reagent);

    ItemStack getResult(List<ItemStack> pedestalItems, ItemStack reagent);
}
