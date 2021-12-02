package com.hollingsworth.arsnouveau.common.armor;

import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.entity.EquipmentSlot;

public class NoviceArmor extends MagicArmor{

    public NoviceArmor(EquipmentSlot slot) {
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
