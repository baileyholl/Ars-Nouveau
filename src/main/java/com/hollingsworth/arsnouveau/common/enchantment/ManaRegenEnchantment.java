package com.hollingsworth.arsnouveau.common.enchantment;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class ManaRegenEnchantment extends Enchantment {

    protected ManaRegenEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentType.ARMOR, new EquipmentSlotType[]{EquipmentSlotType.CHEST, EquipmentSlotType.FEET, EquipmentSlotType.HEAD, EquipmentSlotType.LEGS});
        setRegistryName(ArsNouveau.MODID, "mana_regen");
    }

    @Override
    public int getMinCost(int enchantmentLevel) {
        return 1+11*(enchantmentLevel-1);
    }


    @Override
    public int getMaxLevel() {
        return 3;
    }

}
