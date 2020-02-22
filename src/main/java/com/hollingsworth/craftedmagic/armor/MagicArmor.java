package com.hollingsworth.craftedmagic.armor;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;

public abstract class MagicArmor extends ArmorItem {

    public MagicArmor(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder) {
        super(materialIn, slot, builder);
    }

    public abstract int getMaxManaBonus();

    public abstract int getRegenBonus();
}
