package com.hollingsworth.arsnouveau.api.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class PotionIngredient extends Ingredient {
    private final ItemStack stack;

    public PotionIngredient(ItemStack stack) {
        super(Stream.of(new ItemValue(stack)));
        this.stack = stack;
    }

    public static PotionIngredient fromPotion(Potion potion) {
        ItemStack stack = new ItemStack(Items.POTION);
        PotionUtils.setPotion(stack, potion);
        return new PotionIngredient(stack);
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        if (input == null) {
            return false;
        } else {
            return this.stack.getItem() == input.getItem() && PotionUtils.getPotion(input).equals(PotionUtils.getPotion(stack)) && PotionUtils.getCustomEffects(input).equals(PotionUtils.getCustomEffects(stack));
        }
    }

    @Override
    public boolean isSimple() {
        return false;
    }
}
