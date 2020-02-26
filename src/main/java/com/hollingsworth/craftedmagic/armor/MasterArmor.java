package com.hollingsworth.craftedmagic.armor;

import com.hollingsworth.craftedmagic.items.ModItems;
import net.minecraft.inventory.EquipmentSlotType;

public class MasterArmor  extends MagicArmor{
    public MasterArmor(EquipmentSlotType slot) {
        super(Materials.master, slot, ModItems.defaultItemProperties());
    }

    @Override
    public int getMaxManaBonus() {
        return 80;
    }

    @Override
    public int getRegenBonus() {
        return 6;
    }
}
