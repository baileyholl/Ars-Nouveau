package com.hollingsworth.arsnouveau.datagen;

import com.hollingsworth.arsnouveau.block.BlockRegistry;
import net.minecraft.data.DataGenerator;

public class LootTables extends BaseLootTableProvider{
    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        System.out.println(BlockRegistry.MANA_JAR);
        lootTables.put(BlockRegistry.MANA_JAR, createStandardTable("mana_jar", BlockRegistry.MANA_JAR));
    }
}
