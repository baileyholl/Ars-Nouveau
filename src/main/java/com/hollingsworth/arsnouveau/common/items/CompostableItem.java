package com.hollingsworth.arsnouveau.common.items;

import net.minecraft.block.ComposterBlock;

import net.minecraft.item.Item.Properties;

public class CompostableItem  extends ModItem{
    public CompostableItem(Properties properties, float chance) {
        super(properties);
        ComposterBlock.COMPOSTABLES.putIfAbsent(this, chance);
    }

    public CompostableItem(Properties properties, String registryName, float chance) {
        super(properties, registryName);
        ComposterBlock.COMPOSTABLES.putIfAbsent(this, chance);
    }

    public CompostableItem(String registryName, float chance) {
        super(registryName);
        ComposterBlock.COMPOSTABLES.putIfAbsent(this, chance);
    }
}
