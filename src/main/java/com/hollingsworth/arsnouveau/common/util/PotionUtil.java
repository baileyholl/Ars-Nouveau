package com.hollingsworth.arsnouveau.common.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class PotionUtil {
    public static void addPotionToTag(Potion potionIn, CompoundNBT tag){
        ResourceLocation resourcelocation = Registry.POTION.getKey(potionIn);
        if (potionIn == Potions.EMPTY) {
            if(tag.contains("Potion")) {
                tag.remove("Potion");

            }
        } else {
            tag.putString("Potion", resourcelocation.toString());
        }
    }
}
