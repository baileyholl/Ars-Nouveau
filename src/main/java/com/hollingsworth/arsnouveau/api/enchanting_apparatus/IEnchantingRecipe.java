package com.hollingsworth.arsnouveau.api.enchanting_apparatus;

import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import javax.annotation.Nullable;
import java.util.List;

public interface IEnchantingRecipe extends Recipe<EnchantingApparatusTile> {

    boolean isMatch(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile, @Nullable Player player);

    /**
     * Tile sensitive result
     */
    ItemStack getResult(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile);

    default boolean consumesSource() {
        return getSourceCost() > 0;
    }

    int getSourceCost();
}
