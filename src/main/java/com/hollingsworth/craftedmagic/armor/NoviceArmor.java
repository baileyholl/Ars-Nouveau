package com.hollingsworth.craftedmagic.armor;

import com.hollingsworth.craftedmagic.items.ItemsRegistry;
import net.minecraft.inventory.EquipmentSlotType;

public class NoviceArmor extends MagicArmor{

    public NoviceArmor(EquipmentSlotType slot) {
        super(Materials.novice, slot, ItemsRegistry.defaultItemProperties());
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
