package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.common.block.ArchfruitPod;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class LootTableProvider extends BaseLootTableProvider {
    public LootTableProvider(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        blockTables.put(BlockRegistry.SOURCE_JAR, createManaManchineTable("source_jar", BlockRegistry.SOURCE_JAR));

        LootPool.Builder potionJarBuilder = LootPool.lootPool()
                .name("potion_jar")
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(BlockRegistry.POTION_JAR)
                        .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                        .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                .copy("potionData", "BlockEntityTag.potionData", CopyNbtFunction.MergeStrategy.REPLACE)
                                .copy("currentFill", "BlockEntityTag.currentFill", CopyNbtFunction.MergeStrategy.REPLACE)
                                .copy("locked", "BlockEntityTag.locked", CopyNbtFunction.MergeStrategy.REPLACE))
                        .apply(SetContainerContents.setContents(BlockRegistry.POTION_JAR_TYPE)
                                .withEntry(DynamicLoot.dynamicEntry(new ResourceLocation("minecraft", "contents"))))
                );
        blockTables.put(BlockRegistry.POTION_JAR, LootTable.lootTable().withPool(potionJarBuilder));

        putEntityTable(ModEntities.WILDEN_STALKER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ItemsRegistry.WILDEN_WING.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))))
        );
        putEntityTable(ModEntities.WILDEN_GUARDIAN.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ItemsRegistry.WILDEN_SPIKE.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))))
        );
        putEntityTable(ModEntities.WILDEN_HUNTER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ItemsRegistry.WILDEN_HORN.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))))
        );
        blockTables.put(BlockRegistry.BASTION_POD, LootTable.lootTable().withPool(POD_BUILDER(BlockRegistry.BASTION_POD.asItem(), BlockRegistry.BASTION_POD)));
        blockTables.put(BlockRegistry.MENDOSTEEN_POD, LootTable.lootTable().withPool(POD_BUILDER(BlockRegistry.MENDOSTEEN_POD.asItem(), BlockRegistry.MENDOSTEEN_POD)));
        blockTables.put(BlockRegistry.FROSTAYA_POD, LootTable.lootTable().withPool(POD_BUILDER(BlockRegistry.FROSTAYA_POD.asItem(), BlockRegistry.FROSTAYA_POD)));
        blockTables.put(BlockRegistry.BOMBEGRANTE_POD, LootTable.lootTable().withPool(POD_BUILDER(BlockRegistry.BOMBEGRANTE_POD.asItem(), BlockRegistry.BOMBEGRANTE_POD)));
    }

    public LootPool.Builder POD_BUILDER(Item item, Block block){
        return LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(3.0F))
                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ArchfruitPod.AGE, 2)))));
    }

    public void putEntityTable(EntityType<?> e, LootTable.Builder table) {
        entityTables.put(e.getDefaultLootTable(), table);
    }
}
