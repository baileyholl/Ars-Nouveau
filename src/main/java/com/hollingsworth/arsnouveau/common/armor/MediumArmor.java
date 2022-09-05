package com.hollingsworth.arsnouveau.common.armor;

import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.entity.EquipmentSlot;

@Deprecated(forRemoval = true)
public class MediumArmor extends AnimatedMagicArmor {

    public MediumArmor(EquipmentSlot slot) {
        super(Materials.MEDIUM, slot, ItemsRegistry.defaultItemProperties());
    }

}
