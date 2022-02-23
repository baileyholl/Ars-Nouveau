package com.hollingsworth.arsnouveau.common.items.armor;

import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class MasterArmor  extends MagicArmor{
    public MasterArmor(EquipmentSlot slot) {
        super(Materials.master, slot, ItemsRegistry.defaultItemProperties());
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
