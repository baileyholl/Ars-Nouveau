package com.hollingsworth.arsnouveau.api.enchanting_apparatus;

import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static com.hollingsworth.arsnouveau.api.enchanting_apparatus.ReactiveEnchantmentRecipe.getParchment;

public class SpellWriteRecipe extends EnchantingApparatusRecipe{


    public SpellWriteRecipe(){
        this.pedestalItems = Collections.singletonList(Ingredient.of(ItemsRegistry.SPELL_PARCHMENT));
    }

    @Override
    public boolean isMatch(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile, @Nullable Player player) {
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
        CompoundTag tag = reagent.getOrCreateTag();
        ItemStack parchment = getParchment(pedestalItems);
        tag.putString("spell", parchment.getOrCreateTag().getString("spell"));
        reagent.setTag(tag);
        return reagent.copy();
    }
}
