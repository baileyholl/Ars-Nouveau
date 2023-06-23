package com.hollingsworth.arsnouveau.common.armor;

import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.item.ArmorItem;

public class MediumArmor extends AnimatedMagicArmor {

    public MediumArmor(ArmorItem.Type slot) {
        super(Materials.MEDIUM, slot, ItemsRegistry.defaultItemProperties());
    }

}
