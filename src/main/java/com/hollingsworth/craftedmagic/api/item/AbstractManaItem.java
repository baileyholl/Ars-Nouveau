package com.hollingsworth.craftedmagic.api.item;

import com.hollingsworth.craftedmagic.api.mana.IManaEquipment;
import net.minecraft.item.Item;

public abstract class AbstractManaItem extends Item implements IManaEquipment {

    public AbstractManaItem(Properties properties) {
        super(properties);
    }
}
