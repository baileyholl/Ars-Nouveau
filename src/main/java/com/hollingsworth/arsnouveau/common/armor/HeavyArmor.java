package com.hollingsworth.arsnouveau.common.armor;

import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class HeavyArmor extends AnimatedMagicArmor {
    public HeavyArmor(EquipmentSlot slot) {
        super(Materials.HEAVY, slot, ItemsRegistry.defaultItemProperties());
    }

    @Override
    public int getMaxManaBoost(ItemStack i) {
        return 80;
    }

    @Override
    public int getManaRegenBonus(ItemStack i) {
        return 6;
    }

}
