package com.hollingsworth.arsnouveau.common.armor;

import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class NoviceArmor extends MagicArmor {

    public NoviceArmor(EquipmentSlot slot) {
        super(Materials.novice, slot, ItemsRegistry.defaultItemProperties());
    }

    @Override
    public int getMaxManaBoost(ItemStack i) {
        return 25;
    }

    @Override
    public int getManaRegenBonus(ItemStack i) {
        return 2;
    }
}
