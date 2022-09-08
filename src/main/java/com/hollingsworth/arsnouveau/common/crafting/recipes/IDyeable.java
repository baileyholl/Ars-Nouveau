package com.hollingsworth.arsnouveau.common.crafting.recipes;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public interface IDyeable {
    default void onDye(ItemStack stack, DyeColor dyeColor){
        stack.getOrCreateTag().putInt("color", dyeColor.getId());
    }

    default int getDyeColor(ItemStack stack){
        if(!stack.hasTag()){
            return -1;
        }
        int color = stack.getTag().getInt("color");
        return color;
    }
}
