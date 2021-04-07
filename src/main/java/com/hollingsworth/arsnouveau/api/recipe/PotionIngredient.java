package com.hollingsworth.arsnouveau.api.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class PotionIngredient extends Ingredient {
    private final ItemStack stack;

    public PotionIngredient(ItemStack stack){
        super(Stream.of(new SingleItemList(stack)));
        this.stack = stack;
    }
    public static PotionIngredient fromPotion(Potion potion){
        ItemStack stack = new ItemStack(Items.POTION);
        PotionUtils.addPotionToItemStack(stack, potion);
        return new PotionIngredient(stack);
    }

    public ItemStack getStack(){
        return stack;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        if (input == null) {
            return false;
        } else {
            return this.stack.getItem() == input.getItem() && PotionUtils.getPotionFromItem(input).equals(PotionUtils.getPotionFromItem(stack)) && PotionUtils.getFullEffectsFromItem(input).equals(PotionUtils.getFullEffectsFromItem(stack));
        }
    }

    @Override
    public boolean isSimple() {
        return false;
    }
}
