package com.hollingsworth.arsnouveau.api.potion;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

public interface IPotionProvider {

    PotionContents getPotionData(ItemStack stack);
}
