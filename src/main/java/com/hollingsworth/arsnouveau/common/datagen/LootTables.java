package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.EntityType;
import net.minecraft.loot.*;
import net.minecraft.loot.functions.*;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.level.storage.loot.ConstantIntValue;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetContainerContents;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;

public class LootTables extends BaseLootTableProvider{
    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        System.out.println(BlockRegistry.MANA_JAR);
        blockTables.put(BlockRegistry.MANA_JAR, createManaManchineTable("mana_jar", BlockRegistry.MANA_JAR));

        LootPool.Builder potionJarBuilder = LootPool.lootPool()
                .name("potion_jar")
                .setRolls(ConstantIntValue.exactly(1))
                .add(LootItem.lootTableItem(BlockRegistry.POTION_JAR)
                        .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                        .apply(CopyNbtFunction.copyData(CopyNbtFunction.DataSource.BLOCK_ENTITY)
                                .copy("amount", "BlockEntityTag.amount", CopyNbtFunction.MergeStrategy.REPLACE)
                                .copy("Potion", "BlockEntityTag.Potion", CopyNbtFunction.MergeStrategy.REPLACE)
                                .copy("CustomPotionEffects", "BlockEntityTag.CustomPotionEffects", CopyNbtFunction.MergeStrategy.REPLACE))
                        .apply(SetContainerContents.setContents()
                                .withEntry(DynamicLoot.dynamicEntry(new ResourceLocation("minecraft", "contents"))))
                );
        blockTables.put(BlockRegistry.POTION_JAR,LootTable.lootTable().withPool(potionJarBuilder));
        putStandardLoot(BlockRegistry.ARCANE_ORE);
        putStandardLoot(BlockRegistry.GLYPH_PRESS_BLOCK);
        putStandardLoot(BlockRegistry.WARD_BLOCK);
        putStandardLoot(BlockRegistry.AGRONOMIC_SOURCELINK);
        putStandardLoot(BlockRegistry.ENCHANTING_APP_BLOCK);
        putStandardLoot(BlockRegistry.ARCANE_PEDESTAL);
        putStandardLoot(BlockRegistry.SCRIBES_BLOCK);
        putStandardLoot(BlockRegistry.SUMMONING_CRYSTAL);
        putStandardLoot(BlockRegistry.ARCANE_BRICKS);
        putStandardLoot(BlockRegistry.ARCANE_ROAD);
        putStandardLoot(BlockRegistry.ARCANE_RELAY);
        putStandardLoot(BlockRegistry.ARCANE_RELAY_SPLITTER);
        putStandardLoot(BlockRegistry.ARCANE_CORE_BLOCK);
        putStandardLoot(BlockRegistry.CRYSTALLIZER_BLOCK);
        putStandardLoot(BlockRegistry.VOLCANIC_BLOCK);
        putStandardLoot(BlockRegistry.LAVA_LILY);

        putEntityTable(ModEntities.WILDEN_STALKER,  LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1))
                .add(LootItem.lootTableItem(ItemsRegistry.WILDEN_WING)
                .apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0F, 1.0F)))
                .apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0F, 1.0F)))))
        );
        putEntityTable(ModEntities.WILDEN_GUARDIAN,  LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1))
                        .add(LootItem.lootTableItem(ItemsRegistry.WILDEN_SPIKE)
                                .apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0F, 1.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0F, 1.0F)))))
        );
        putEntityTable(ModEntities.WILDEN_HUNTER,  LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1))
                        .add(LootItem.lootTableItem(ItemsRegistry.WILDEN_HORN)
                                .apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0F, 1.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0F, 1.0F)))))
        );
        putStandardLoot(BlockRegistry.RELAY_WARP);
        putStandardLoot(BlockRegistry.RELAY_DEPOSIT);
        //LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.BONE).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))))
       // putStandardLoot(BlockRegistry.ARCANE_BRICKS);
    }

    public void putStandardLoot(Block block){
        blockTables.put(block, createStandardTable(block.getRegistryName().toString().replace(ArsNouveau.MODID + ":", "") , block));
    }

    public void putEntityTable(EntityType e, LootTable.Builder table){
        entityTables.put(e.getDefaultLootTable(), table);
    }



}
