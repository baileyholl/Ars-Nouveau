package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import net.minecraft.item.Item;

import net.minecraft.item.Item.Properties;

public abstract class AbstractManaItem extends Item implements IManaEquipment {

    public AbstractManaItem(Properties properties) {
        super(properties);
    }
}
