package com.hollingsworth.arsnouveau.api.enchanting_apparatus;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class ReactiveEnchantmentRecipe extends EnchantmentRecipe{

    public ReactiveEnchantmentRecipe(ItemStack[] pedestalItems, int manaCost) {
        super(pedestalItems,  EnchantmentRegistry.REACTIVE_ENCHANTMENT, 1, manaCost);
    }

    @Override
    public boolean isMatch(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile) {
        ItemStack parchment = getParchment(pedestalItems);
        return super.isMatch(pedestalItems, reagent, enchantingApparatusTile) && parchment != null && SpellParchment.getSpellRecipe(parchment) != null;
    }

    public static ItemStack getParchment(List<ItemStack> pedestalItems){
        for(ItemStack stack : pedestalItems){
            if(stack.getItem() instanceof SpellParchment){
                return stack;
            }
        }
        return null;
    }

    @Override
    public ItemStack getResult(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile) {
        ItemStack stack = super.getResult(pedestalItems, reagent, enchantingApparatusTile);
        CompoundNBT tag = stack.getTag();
        ItemStack parchment = getParchment(pedestalItems);
        tag.putString("spell", parchment.getTag().getString("spell"));
        stack.setTag(tag);
        return stack;
    }

    @Override
    public ResourceLocation getId() {
        return new ResourceLocation(ArsNouveau.MODID, "reactive");
    }

}
