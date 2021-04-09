package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.*;
import net.minecraft.loot.functions.*;
import net.minecraft.util.ResourceLocation;

public class LootTables extends BaseLootTableProvider{
    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        System.out.println(BlockRegistry.MANA_JAR);
        blockTables.put(BlockRegistry.MANA_JAR, createManaManchineTable("mana_jar", BlockRegistry.MANA_JAR));

        LootPool.Builder potionJarBuilder = LootPool.builder()
                .name("potion_jar")
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(BlockRegistry.POTION_JAR)
                        .acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
                        .acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY)
                                .addOperation("amount", "BlockEntityTag.amount", CopyNbt.Action.REPLACE)
                                .addOperation("Potion", "BlockEntityTag.Potion", CopyNbt.Action.REPLACE)
                                .addOperation("CustomPotionEffects", "BlockEntityTag.CustomPotionEffects", CopyNbt.Action.REPLACE))
                        .acceptFunction(SetContents.builderIn()
                                .addLootEntry(DynamicLootEntry.func_216162_a(new ResourceLocation("minecraft", "contents"))))
                );
        blockTables.put(BlockRegistry.POTION_JAR,LootTable.builder().addLootPool(potionJarBuilder));
        putStandardLoot(BlockRegistry.ARCANE_ORE);
        putStandardLoot(BlockRegistry.GLYPH_PRESS_BLOCK);
        putStandardLoot(BlockRegistry.WARD_BLOCK);
        putStandardLoot(BlockRegistry.MANA_CONDENSER);
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

        putEntityTable(ModEntities.WILDEN_STALKER,  LootTable.builder()
                .addLootPool(LootPool.builder().rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(ItemsRegistry.WILDEN_WING)
                .acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F)))
                .acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))))
        );
        putEntityTable(ModEntities.WILDEN_GUARDIAN,  LootTable.builder()
                .addLootPool(LootPool.builder().rolls(ConstantRange.of(1))
                        .addEntry(ItemLootEntry.builder(ItemsRegistry.WILDEN_SPIKE)
                                .acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F)))
                                .acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))))
        );
        putEntityTable(ModEntities.WILDEN_HUNTER,  LootTable.builder()
                .addLootPool(LootPool.builder().rolls(ConstantRange.of(1))
                        .addEntry(ItemLootEntry.builder(ItemsRegistry.WILDEN_HORN)
                                .acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F)))
                                .acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))))
        );
        //LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.BONE).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))))
       // putStandardLoot(BlockRegistry.ARCANE_BRICKS);
    }

    public void putStandardLoot(Block block){
        blockTables.put(block, createStandardTable(block.getRegistryName().toString().replace(ArsNouveau.MODID + ":", "") , block));
    }

    public void putEntityTable(EntityType e, LootTable.Builder table){
        entityTables.put(e.getLootTable(), table);
    }



}
