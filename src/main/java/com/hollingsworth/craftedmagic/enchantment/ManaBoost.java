package com.hollingsworth.craftedmagic.enchantment;

import com.hollingsworth.craftedmagic.ArsNouveau;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class ManaBoost extends Enchantment {
    protected ManaBoost() {
        super(Rarity.UNCOMMON, EnchantmentType.ARMOR, new EquipmentSlotType[]{EquipmentSlotType.CHEST, EquipmentSlotType.FEET, EquipmentSlotType.HEAD, EquipmentSlotType.LEGS});
        setRegistryName(ArsNouveau.MODID, "mana_boost");
    }
    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 20;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return 100;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
