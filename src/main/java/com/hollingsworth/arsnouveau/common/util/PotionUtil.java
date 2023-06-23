package com.hollingsworth.arsnouveau.common.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.registries.ForgeRegistries;

public class PotionUtil {
    public static void addPotionToTag(Potion potionIn, CompoundTag tag) {
        ResourceLocation resourcelocation = ForgeRegistries.POTIONS.getKey(potionIn);
        if (potionIn == Potions.EMPTY) {
            if (tag.contains("Potion")) {
                tag.remove("Potion");

            }
        } else {
            tag.putString("Potion", resourcelocation.toString());
        }
    }
}
