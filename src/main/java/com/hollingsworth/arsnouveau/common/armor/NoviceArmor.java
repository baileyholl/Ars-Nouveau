package com.hollingsworth.arsnouveau.common.armor;

import com.hollingsworth.arsnouveau.setup.Config;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class NoviceArmor extends MagicArmor{

    public NoviceArmor(EquipmentSlot slot) {
        super(Materials.novice, slot, ItemsRegistry.defaultItemProperties());
    }

    @Override
    public int getMaxManaBoost(ItemStack i) {
        return Config.NOVIVE_ARMOR_MANA_BOOST.get();
    }

    @Override
    public int getManaRegenBonus(ItemStack i) {
        return Config.NOVICE_ARMOR_MANA_REGEN_BONUS.get();
    }
}
