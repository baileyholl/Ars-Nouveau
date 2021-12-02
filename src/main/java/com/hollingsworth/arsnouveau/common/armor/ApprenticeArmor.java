package com.hollingsworth.arsnouveau.common.armor;

import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.entity.EquipmentSlot;

public class ApprenticeArmor extends MagicArmor{
    public ApprenticeArmor(EquipmentSlot slot) {
        super(Materials.apprentice, slot, ItemsRegistry.defaultItemProperties());
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
