package com.hollingsworth.arsnouveau.common.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ReactiveEnchantment extends Enchantment {
    public ReactiveEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEARABLE, new EquipmentSlot[]{EquipmentSlot.CHEST, EquipmentSlot.FEET, EquipmentSlot.HEAD, EquipmentSlot.LEGS});
    }

    @Override
    public int getMinCost(int enchantmentLevel) {
        return 0;
    }

    @Override
    public int getMaxCost(int enchantmentLevel) {
        return 0;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return false;
    }
}
