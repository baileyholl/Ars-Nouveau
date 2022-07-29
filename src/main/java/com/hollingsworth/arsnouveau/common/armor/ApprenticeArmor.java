package com.hollingsworth.arsnouveau.common.armor;

import com.hollingsworth.arsnouveau.setup.Config;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ApprenticeArmor extends MagicArmor{
    public ApprenticeArmor(EquipmentSlot slot) {
        super(Materials.apprentice, slot, ItemsRegistry.defaultItemProperties());
    }

    @Override
    public int getMaxManaBoost(ItemStack i) {
        return Config.APPRENTICE_ARMOR_MANA_BOOST.get();
    }

    @Override
    public int getManaRegenBonus(ItemStack i) {
        return Config.APPRENTICE_ARMOR_MANA_REGEN_BONUS.get();
    }
}
