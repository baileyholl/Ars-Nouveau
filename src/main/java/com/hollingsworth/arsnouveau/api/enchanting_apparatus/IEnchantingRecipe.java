package com.hollingsworth.arsnouveau.api.enchanting_apparatus;

import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;

public interface IEnchantingRecipe extends IRecipe<EnchantingApparatusTile> {

    boolean isMatch(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile);

    /**
     * Tile sensitive result
     */
    ItemStack getResult(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile);

    default boolean consumesMana(){
        return manaCost() > 0;
    }

    default boolean canCraft(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile){
        return isMatch(pedestalItems, reagent, enchantingApparatusTile) && (!consumesMana() || ManaUtil.hasManaNearby(enchantingApparatusTile.getPos(), enchantingApparatusTile.getWorld(), 10, manaCost()));
    }

    int manaCost();
}
