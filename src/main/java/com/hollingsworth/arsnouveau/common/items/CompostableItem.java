package com.hollingsworth.arsnouveau.common.items;

import net.minecraft.block.ComposterBlock;

public class CompostableItem  extends ModItem{
    public CompostableItem(Properties properties, float chance) {
        super(properties);
        ComposterBlock.CHANCES.putIfAbsent(this, chance);
    }

    public CompostableItem(Properties properties, String registryName, float chance) {
        super(properties, registryName);
        ComposterBlock.CHANCES.putIfAbsent(this, chance);
    }

    public CompostableItem(String registryName, float chance) {
        super(registryName);
        ComposterBlock.CHANCES.putIfAbsent(this, chance);
    }
}
