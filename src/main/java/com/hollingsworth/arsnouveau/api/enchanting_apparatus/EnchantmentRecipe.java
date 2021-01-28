package com.hollingsworth.arsnouveau.api.enchanting_apparatus;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EnchantmentRecipe extends EnchantingApparatusRecipe{
    public Enchantment enchantment;
    public int enchantLevel;

    public EnchantmentRecipe(List<Ingredient> pedestalItems, Enchantment enchantment, int level, int manaCost){
        this.pedestalItems = pedestalItems;
        this.enchantment = enchantment;
        this.enchantLevel = level;
        this.manaCost = manaCost;
        this.id = new ResourceLocation(ArsNouveau.MODID, result.getItem().getRegistryName().getPath());
    }

    public EnchantmentRecipe(ItemStack[] pedestalItems, Enchantment enchantment, int level, int manaCost){
        List<Ingredient> ingres = new ArrayList<>();
        for(ItemStack i : pedestalItems){
            ingres.add(Ingredient.fromItems(i.getItem()));
        }
        this.pedestalItems = ingres;
        this.enchantment = enchantment;
        this.enchantLevel = level;
        this.manaCost = manaCost;
        this.id = new ResourceLocation(ArsNouveau.MODID, result.getItem().getRegistryName().getPath());
    }

    public EnchantmentRecipe(Ingredient[] pedestalItems, Enchantment enchantment, int level, int manaCost){
        this.pedestalItems =  Arrays.asList(pedestalItems);
        this.enchantment = enchantment;
        this.enchantLevel = level;
        this.manaCost = manaCost;
        this.id = new ResourceLocation(ArsNouveau.MODID, result.getItem().getRegistryName().getPath());
    }

    @Override
    public boolean doesReagentMatch(ItemStack stack) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        int level = enchantments.getOrDefault(enchantment, 0);
        return enchantment.canApply(stack) && this.enchantLevel - level == 1;
    }

    @Override
    public ItemStack getCraftingResult(EnchantingApparatusTile inv) {
        ItemStack stack = inv.catalystItem.copy();
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        enchantments.put(enchantment, enchantLevel);
        EnchantmentHelper.setEnchantments(enchantments, stack);
        return stack;
    }


    @Override
    public ItemStack getResult(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile tile) {
        return getCraftingResult(tile);
    }
}
