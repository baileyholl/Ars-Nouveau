package com.hollingsworth.arsnouveau.common.util;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

public class PotionUtil {
    public static void addPotionToTag(Potion potionIn, CompoundTag tag) {
        ResourceLocation resourcelocation = BuiltInRegistries.POTION.getKey(potionIn);
        if (potionIn == PotionContents.EMPTY) {
            if (tag.contains("Potion")) {
                tag.remove("Potion");

            }
        } else {
            tag.putString("Potion", resourcelocation.toString());
        }
    }

    public static ItemStack getPotion(Holder<Potion> potion) {
        ItemStack stack = new ItemStack(Items.POTION);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
        return stack;
    }

    public static Ingredient getIngredient(Holder<Potion> potion) {
        ItemStack stack = getPotion(potion);
        return PotionUtil.getIngredient(stack);
    }

    public static Ingredient getIngredient(ItemStack input) {
        return DataComponentIngredient.of(false, input);
    }
}
