package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.common.block.BlockRegistry;
import net.minecraft.data.DataGenerator;

public class LootTables extends BaseLootTableProvider{
    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        System.out.println(BlockRegistry.MANA_JAR);
        lootTables.put(BlockRegistry.MANA_JAR, createManaManchineTable("mana_jar", BlockRegistry.MANA_JAR));
        lootTables.put(BlockRegistry.ARCANE_ORE, createStandardTable("mana_ore", BlockRegistry.ARCANE_ORE));
    }
}
