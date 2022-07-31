package com.hollingsworth.arsnouveau.common.armor;

import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class MediumArmor extends AnimatedMagicArmor {

    public MediumArmor(EquipmentSlot slot) {
        super(Materials.MEDIUM, slot, ItemsRegistry.defaultItemProperties());
    }

    @Override
    public int getMaxManaBoost(ItemStack i) {
        return 40;
    }

    @Override
    public int getManaRegenBonus(ItemStack i) {
        return 4;
    }
}
