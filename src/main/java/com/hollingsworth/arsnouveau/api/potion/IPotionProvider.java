package com.hollingsworth.arsnouveau.api.potion;

import net.minecraft.world.item.ItemStack;

public interface IPotionProvider {

    PotionData getPotionData(ItemStack stack);
}
