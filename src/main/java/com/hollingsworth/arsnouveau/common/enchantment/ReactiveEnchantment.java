package com.hollingsworth.arsnouveau.common.enchantment;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class ReactiveEnchantment extends Enchantment {
    protected ReactiveEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentType.WEARABLE, new EquipmentSlotType[]{EquipmentSlotType.CHEST, EquipmentSlotType.FEET, EquipmentSlotType.HEAD, EquipmentSlotType.LEGS});
        setRegistryName(ArsNouveau.MODID, "reactive");
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 0;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return 0;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canApply(ItemStack stack) {
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
