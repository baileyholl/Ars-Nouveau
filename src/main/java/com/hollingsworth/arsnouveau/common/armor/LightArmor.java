package com.hollingsworth.arsnouveau.common.armor;

import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.entity.EquipmentSlot;

@Deprecated(forRemoval = true)
public class LightArmor extends AnimatedMagicArmor {

    public LightArmor(EquipmentSlot slot) {
        super(Materials.LIGHT, slot, ItemsRegistry.defaultItemProperties());
    }

}
