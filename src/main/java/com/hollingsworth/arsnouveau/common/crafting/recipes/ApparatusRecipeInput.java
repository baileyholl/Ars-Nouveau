package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import javax.annotation.Nullable;
import java.util.List;

public record ApparatusRecipeInput(ItemStack catalyst, List<ItemStack> pedestals,
                                   @Nullable Player player) implements RecipeInput {

    public ApparatusRecipeInput(EnchantingApparatusTile tile) {
        this(tile.getStack(), tile.getPedestalItems(), null);
    }

    public ApparatusRecipeInput(EnchantingApparatusTile tile, @Nullable Player player) {
        this(tile.getStack(), tile.getPedestalItems(), player);
    }

    @Override
    public ItemStack getItem(int p_346128_) {
        return pedestals.get(p_346128_);
    }

    @Override
    public int size() {
        return pedestals.size();
    }
}
