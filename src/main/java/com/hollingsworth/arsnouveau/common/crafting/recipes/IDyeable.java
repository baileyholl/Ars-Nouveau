package com.hollingsworth.arsnouveau.common.crafting.recipes;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

public interface IDyeable {

    default void onDye(ItemStack stack, DyeColor dyeColor){
        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(dyeColor.getTextureDiffuseColor(), false));
    }

}
