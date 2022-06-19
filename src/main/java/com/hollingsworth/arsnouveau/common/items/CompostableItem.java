package com.hollingsworth.arsnouveau.common.items;

import net.minecraft.world.level.block.ComposterBlock;

public class CompostableItem  extends ModItem{
    public CompostableItem(Properties properties, float chance) {
        super(properties);
        ComposterBlock.COMPOSTABLES.putIfAbsent(this, chance);
    }

    public CompostableItem(float chance) {
        super();
        ComposterBlock.COMPOSTABLES.putIfAbsent(this, chance);
    }
}
