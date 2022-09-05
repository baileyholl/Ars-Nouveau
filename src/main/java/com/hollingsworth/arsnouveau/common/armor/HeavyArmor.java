package com.hollingsworth.arsnouveau.common.armor;

import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.entity.EquipmentSlot;

@Deprecated(forRemoval = true)
public class HeavyArmor extends AnimatedMagicArmor {

    public HeavyArmor(EquipmentSlot slot) {
        super(Materials.HEAVY, slot, ItemsRegistry.defaultItemProperties());
    }

}
