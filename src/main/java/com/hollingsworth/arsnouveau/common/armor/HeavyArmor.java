package com.hollingsworth.arsnouveau.common.armor;

import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.item.ArmorItem;

public class HeavyArmor extends AnimatedMagicArmor {

    public HeavyArmor(ArmorItem.Type slot) {
        super(Materials.HEAVY, slot, ItemsRegistry.defaultItemProperties());
    }

}
