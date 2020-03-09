package com.hollingsworth.craftedmagic.armor;

import com.hollingsworth.craftedmagic.api.mana.IManaEquipment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;

public abstract class MagicArmor extends ArmorItem implements IManaEquipment {

    public MagicArmor(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder) {
        super(materialIn, slot, builder);
    }
}
