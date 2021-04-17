package com.hollingsworth.arsnouveau.api.enchanting_apparatus;

import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;

import java.util.Arrays;
import java.util.List;

import static com.hollingsworth.arsnouveau.api.enchanting_apparatus.ReactiveEnchantmentRecipe.getParchment;

public class SpellWriteRecipe extends EnchantingApparatusRecipe{


    public SpellWriteRecipe(){
        this.pedestalItems = Arrays.asList(new Ingredient[]{Ingredient.of(ItemsRegistry.spellParchment)});
    }

    @Override
    public boolean isMatch(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile) {
        int level = EnchantmentHelper.getEnchantments(reagent).getOrDefault(EnchantmentRegistry.REACTIVE_ENCHANTMENT, 0);
        ItemStack parchment = getParchment(pedestalItems);
        return parchment != null && SpellParchment.getSpellRecipe(parchment) != null && level > 0 && super.isMatch(pedestalItems, reagent, enchantingApparatusTile);
    }

    @Override
    public boolean doesReagentMatch(ItemStack reag) {
        return true;
    }

    @Override
    public ItemStack getResult(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile) {
        CompoundNBT tag = reagent.getTag();
        ItemStack parchment = getParchment(pedestalItems);
        tag.putString("spell", parchment.getTag().getString("spell"));
        reagent.setTag(tag);
        return reagent.copy();
    }
}
