package com.hollingsworth.arsnouveau.common.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ManaBoost extends Enchantment {
    public ManaBoost() {
        super(Rarity.UNCOMMON, EnchantmentCategory.ARMOR, new EquipmentSlot[]{EquipmentSlot.CHEST, EquipmentSlot.FEET, EquipmentSlot.HEAD, EquipmentSlot.LEGS});
    }

    @Override
    public int getMinCost(int enchantmentLevel) {
        return 1 + 11 * (enchantmentLevel - 1);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

}
