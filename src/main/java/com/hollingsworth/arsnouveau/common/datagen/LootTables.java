package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.BlockRegistry;
import com.hollingsworth.arsnouveau.common.block.GlyphPressBlock;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.storage.loot.LootTable;

public class LootTables extends BaseLootTableProvider{
    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        System.out.println(BlockRegistry.MANA_JAR);
        lootTables.put(BlockRegistry.MANA_JAR, createManaManchineTable("mana_jar", BlockRegistry.MANA_JAR));
        lootTables.put(BlockRegistry.ARCANE_ORE, createStandardTable("mana_ore", BlockRegistry.ARCANE_ORE));

        lootTables.put(BlockRegistry.GLYPH_PRESS_BLOCK, createStandardTable("mana_ore", BlockRegistry.ARCANE_ORE));
        putStandardLoot(BlockRegistry.GLYPH_PRESS_BLOCK);
        putStandardLoot(BlockRegistry.WARD_BLOCK);
        putStandardLoot(BlockRegistry.MANA_CONDENSER);
        putStandardLoot(BlockRegistry.ENCHANTING_APP_BLOCK);
        putStandardLoot(BlockRegistry.ARCANE_PEDESTAL);
    }

    public void putStandardLoot(Block block){
        lootTables.put(block, createStandardTable(block.getRegistryName().toString().replace(ArsNouveau.MODID + ":", "") , block));
    }

}
