package com.hollingsworth.craftedmagic.armor;

import com.hollingsworth.craftedmagic.items.ModItems;
import net.minecraft.inventory.EquipmentSlotType;

public class ApprenticeArmor extends MagicArmor{
    public ApprenticeArmor(EquipmentSlotType slot) {
        super(Materials.apprentice, slot, ModItems.defaultItemProperties());
    }

    @Override
    public int getMaxManaBoost() {
        return 40;
    }

    @Override
    public int getManaRegenBonus() {
        return 4;
    }
}
