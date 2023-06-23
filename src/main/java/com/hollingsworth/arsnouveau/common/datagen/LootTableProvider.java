package com.hollingsworth.arsnouveau.common.datagen;

//
//public class LootTableProvider extends BaseLootTableProvider {
//    public LootTableProvider(DataGenerator dataGeneratorIn) {
//        super(dataGeneratorIn);
//    }
//
//    @Override
//    protected void addTables() {
//        blockTables.put(BlockRegistry.SOURCE_JAR, createManaManchineTable("source_jar", BlockRegistry.SOURCE_JAR));
//
//        LootPool.Builder potionJarBuilder = LootPool.lootPool()
//                .name("potion_jar")
//                .setRolls(ConstantValue.exactly(1))
//                .add(LootItem.lootTableItem(BlockRegistry.POTION_JAR)
//                        .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
//                        .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
//                                .copy("potionData", "BlockEntityTag.potionData", CopyNbtFunction.MergeStrategy.REPLACE)
//                                .copy("currentFill", "BlockEntityTag.currentFill", CopyNbtFunction.MergeStrategy.REPLACE)
//                                .copy("locked", "BlockEntityTag.locked", CopyNbtFunction.MergeStrategy.REPLACE)
//                                .copy("potionNames", "potionNames", CopyNbtFunction.MergeStrategy.REPLACE)
//                                .copy("currentFill", "fill", CopyNbtFunction.MergeStrategy.REPLACE))
//                        .apply(SetContainerContents.setContents(BlockRegistry.POTION_JAR_TYPE)
//                                .withEntry(DynamicLoot.dynamicEntry(new ResourceLocation("minecraft", "contents"))))
//                );
//        blockTables.put(BlockRegistry.POTION_JAR, LootTable.lootTable().withPool(potionJarBuilder));
//
//        putEntityTable(ModEntities.WILDEN_STALKER.get(), LootTable.lootTable()
//                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
//                        .add(LootItem.lootTableItem(ItemsRegistry.WILDEN_WING.get())
//                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
//                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))))
//        );
//        putEntityTable(ModEntities.WILDEN_GUARDIAN.get(), LootTable.lootTable()
//                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
//                        .add(LootItem.lootTableItem(ItemsRegistry.WILDEN_SPIKE.get())
//                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
//                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))))
//        );
//        putEntityTable(ModEntities.WILDEN_HUNTER.get(), LootTable.lootTable()
//                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
//                        .add(LootItem.lootTableItem(ItemsRegistry.WILDEN_HORN.get())
//                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
//                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))))
//        );
//        blockTables.put(BlockRegistry.BASTION_POD, LootTable.lootTable().withPool(POD_BUILDER(BlockRegistry.BASTION_POD.asItem(), BlockRegistry.BASTION_POD)));
//        blockTables.put(BlockRegistry.MENDOSTEEN_POD, LootTable.lootTable().withPool(POD_BUILDER(BlockRegistry.MENDOSTEEN_POD.asItem(), BlockRegistry.MENDOSTEEN_POD)));
//        blockTables.put(BlockRegistry.FROSTAYA_POD, LootTable.lootTable().withPool(POD_BUILDER(BlockRegistry.FROSTAYA_POD.asItem(), BlockRegistry.FROSTAYA_POD)));
//        blockTables.put(BlockRegistry.BOMBEGRANTE_POD, LootTable.lootTable().withPool(POD_BUILDER(BlockRegistry.BOMBEGRANTE_POD.asItem(), BlockRegistry.BOMBEGRANTE_POD)));
//
//
//        LootPool.Builder mobJarBuilder = LootPool.lootPool()
//                .name("mob_jar")
//                .setRolls(ConstantValue.exactly(1))
//                .add(LootItem.lootTableItem(BlockRegistry.MOB_JAR)
//                        .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
//                        .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
//                                .copy("entityTag", "BlockEntityTag.entityTag", CopyNbtFunction.MergeStrategy.REPLACE)
//                                .copy("entityId", "entityId", CopyNbtFunction.MergeStrategy.REPLACE))
//                        .apply(SetContainerContents.setContents(BlockRegistry.MOB_JAR_TILE)
//                                .withEntry(DynamicLoot.dynamicEntry(new ResourceLocation("minecraft", "contents"))))
//                );
//        blockTables.put(BlockRegistry.MOB_JAR, LootTable.lootTable().withPool(mobJarBuilder));
//        //CustomName
//        LootPool.Builder repository = LootPool.lootPool()
//                .name("repository")
//                .setRolls(ConstantValue.exactly(1))
//                .add(LootItem.lootTableItem(BlockRegistry.REPOSITORY)
//                        .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY)));
//        blockTables.put(BlockRegistry.REPOSITORY, LootTable.lootTable().withPool(repository));
//    }
//
//    public LootPool.Builder POD_BUILDER(Item item, Block block){
//        return LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
//                .add(LootItem.lootTableItem(item)
//                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(3.0F))
//                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
//                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ArchfruitPod.AGE, 2)))));
//    }
//
//    public void putEntityTable(EntityType<?> e, LootTable.Builder table) {
//        entityTables.put(e.getDefaultLootTable(), table);
//    }
//}
