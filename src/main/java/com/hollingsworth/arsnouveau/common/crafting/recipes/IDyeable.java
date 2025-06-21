package com.hollingsworth.arsnouveau.common.crafting.recipes;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public interface IDyeable {

    default void onDye(ItemStack stack, DyeColor dyeColor) {
        stack.set(DataComponents.BASE_COLOR, dyeColor);
    }

}
