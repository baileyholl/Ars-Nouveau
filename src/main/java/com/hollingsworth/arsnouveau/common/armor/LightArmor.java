package com.hollingsworth.arsnouveau.common.armor;

import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.item.ArmorItem;

public class LightArmor extends AnimatedMagicArmor {

    public LightArmor(ArmorItem.Type slot) {
        super(Materials.LIGHT, slot, ItemsRegistry.defaultItemProperties());
    }

}
