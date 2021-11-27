package com.hollingsworth.arsnouveau.api.enchanting_apparatus;

import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static com.hollingsworth.arsnouveau.api.enchanting_apparatus.ReactiveEnchantmentRecipe.getParchment;

public class SpellWriteRecipe extends EnchantingApparatusRecipe{


    public SpellWriteRecipe(){
        this.pedestalItems = Collections.singletonList(Ingredient.of(ItemsRegistry.spellParchment));
    }

    @Override
    public boolean isMatch(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile, @Nullable PlayerEntity player) {
        int level = EnchantmentHelper.getEnchantments(reagent).getOrDefault(EnchantmentRegistry.REACTIVE_ENCHANTMENT, 0);
        ItemStack parchment = getParchment(pedestalItems);
        return !parchment.isEmpty() && !SpellParchment.getSpell(parchment).isEmpty() && level > 0 && super.isMatch(pedestalItems, reagent, enchantingApparatusTile, player);
    }

    @Override
    public boolean doesReagentMatch(ItemStack reag) {
        return true;
    }

    @Override
    public ItemStack getResult(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile) {
        CompoundNBT tag = reagent.getOrCreateTag();
        ItemStack parchment = getParchment(pedestalItems);
        tag.putString("spell", parchment.getOrCreateTag().getString("spell"));
        reagent.setTag(tag);
        return reagent.copy();
    }
}
