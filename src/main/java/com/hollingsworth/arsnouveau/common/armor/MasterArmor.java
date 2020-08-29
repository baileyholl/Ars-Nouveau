package com.hollingsworth.arsnouveau.common.armor;

import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.inventory.EquipmentSlotType;

public class MasterArmor  extends MagicArmor{
    public MasterArmor(EquipmentSlotType slot) {
        super(Materials.master, slot, ItemsRegistry.defaultItemProperties());
    }

    @Override
    public int getMaxManaBoost() {
        return 80;
    }

    @Override
    public int getManaRegenBonus() {
        return 6;
    }
}
