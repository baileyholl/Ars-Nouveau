package com.hollingsworth.arsnouveau.api.enchanting_apparatus;

import com.hollingsworth.arsnouveau.common.items.ModItem;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class AugmentJewelryRecipe implements IEnchantingRecipe{
    public final Item starterItem; // Item for crafting from a plain old item to the first tier augment (dull trinket, ring, etc).
    public final Item augmentedItem; // Item that holds the augment data (augment ring, necklace, etc.)

    public AugmentJewelryRecipe(Item starterItem, Item augmentedItem){
        this.starterItem = starterItem;
        this.augmentedItem = augmentedItem;
    }

    @Override
    public boolean isMatch(List<ItemStack> pedestalItems, ItemStack reagent) {
        if(reagent.getItem() == starterItem){

        }
        return false;
    }

    public int getRequiredGlyphs(ItemStack stack){
        return 1;
    }

    @Override
    public ItemStack getResult(List<ItemStack> pedestalItems, ItemStack reagent) {
        return null;
    }
}
