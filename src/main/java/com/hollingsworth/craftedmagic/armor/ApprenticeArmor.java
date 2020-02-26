package com.hollingsworth.craftedmagic.armor;

import com.hollingsworth.craftedmagic.items.ModItems;
import net.minecraft.inventory.EquipmentSlotType;

public class ApprenticeArmor extends MagicArmor{
    public ApprenticeArmor(EquipmentSlotType slot) {
        super(Materials.apprentice, slot, ModItems.defaultItemProperties());
    }

    @Override
    public int getMaxManaBonus() {
        return 40;
    }

    @Override
    public int getRegenBonus() {
        return 4;
    }
}
