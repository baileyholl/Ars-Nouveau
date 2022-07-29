package com.hollingsworth.arsnouveau.common.armor;

import com.hollingsworth.arsnouveau.setup.Config;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class MasterArmor  extends MagicArmor{
    public MasterArmor(EquipmentSlot slot) {
        super(Materials.master, slot, ItemsRegistry.defaultItemProperties());
    }

    @Override
    public int getMaxManaBoost(ItemStack i) {
        return Config.MASTER_ARMOR_MANA_BOOST.get();
    }

    @Override
    public int getManaRegenBonus(ItemStack i) {
        return Config.MASTER_ARMOR_MANA_REGEN_BONUS.get();
    }

}
