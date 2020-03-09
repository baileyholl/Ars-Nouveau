package com.hollingsworth.craftedmagic.armor;

import com.hollingsworth.craftedmagic.items.ModItems;
import net.minecraft.inventory.EquipmentSlotType;

public class NoviceArmor extends MagicArmor{

    public NoviceArmor(EquipmentSlotType slot) {
        super(Materials.novice, slot, ModItems.defaultItemProperties());
    }

    @Override
    public int getMaxManaBoost() {
        return 25;
    }

    @Override
    public int getManaRegenBonus() {
        return 2;
    }
}
