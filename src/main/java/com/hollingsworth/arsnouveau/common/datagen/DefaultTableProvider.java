package com.hollingsworth.arsnouveau.common.datagen;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.AlterationTable;
import com.hollingsworth.arsnouveau.common.block.ArchfruitPod;
import com.hollingsworth.arsnouveau.common.block.ScribesBlock;
import com.hollingsworth.arsnouveau.common.block.ThreePartBlock;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.util.RegistryWrapper;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultTableProvider extends LootTableProvider {
    public DefaultTableProvider(PackOutput pOutput) {
        super(pOutput, new HashSet<>(), List.of(new LootTableProvider.SubProviderEntry(BlockLootTable::new, LootContextParamSets.BLOCK), new LootTableProvider.SubProviderEntry(EntityLootTable::new, LootContextParamSets.ENTITY)));
    }

    private static final float[] DEFAULT_SAPLING_DROP_RATES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};

    public static class BlockLootTable extends BlockLootSubProvider {
        public List<Block> list = new ArrayList<>();

        protected BlockLootTable() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), new HashMap<>());
        }

        @Override
        protected void generate() {
            registerDropSelf(BlockRegistry.ENCHANTED_SPELL_TURRET);

            registerDropSelf(BlockRegistry.BLAZING_LOG);
            registerDropSelf(BlockRegistry.VEXING_LOG);
            registerDropSelf(BlockRegistry.CASCADING_LOG);
            registerDropSelf(BlockRegistry.FLOURISHING_LOG);

            registerDropSelf(BlockRegistry.BLAZING_SAPLING);
            registerDropSelf(BlockRegistry.VEXING_SAPLING);
            registerDropSelf(BlockRegistry.CASCADING_SAPLING);
            registerDropSelf(BlockRegistry.FLOURISHING_SAPLING);
            registerDropSelf(BlockRegistry.ARCHWOOD_PLANK);
            registerDropSelf(BlockRegistry.CREATIVE_SOURCE_JAR);

            registerDrop(BlockRegistry.WIXIE_CAULDRON.get(), Items.CAULDRON);
            for (FlowerPotBlock pot : BlockRegistry.flowerPots.values()) {
                list.add(pot);
                dropPottedContents(pot);
            }

            registerLeavesAndSticks(BlockRegistry.BLAZING_LEAVES.get(), BlockRegistry.BLAZING_SAPLING.get());
            registerLeavesAndSticks(BlockRegistry.CASCADING_LEAVE.get(), BlockRegistry.CASCADING_SAPLING.get());
            registerLeavesAndSticks(BlockRegistry.FLOURISHING_LEAVES.get(), BlockRegistry.FLOURISHING_SAPLING.get());
            registerLeavesAndSticks(BlockRegistry.VEXING_LEAVES.get(), BlockRegistry.VEXING_SAPLING.get());


            registerDropSelf(BlockRegistry.BLAZING_WOOD);
            registerDropSelf(BlockRegistry.VEXING_WOOD);
            registerDropSelf(BlockRegistry.CASCADING_WOOD);
            registerDropSelf(BlockRegistry.FLOURISHING_WOOD);

            registerDropSelf(BlockRegistry.ARCHWOOD_BUTTON);
            registerDropSelf(BlockRegistry.ARCHWOOD_STAIRS);
            registerSlabItemTable(BlockRegistry.ARCHWOOD_SLABS.get());
            registerDropSelf(BlockRegistry.MAGELIGHT_TORCH);

            // registerDropSelf(BlockRegistry.ARCHWOOD_SIGN);
            registerDropSelf(BlockRegistry.ARCHWOOD_FENCE_GATE);
            registerDropSelf(BlockRegistry.ARCHWOOD_TRAPDOOR);
            registerDropSelf(BlockRegistry.ARCHWOOD_PPlate);
            registerDropSelf(BlockRegistry.ARCHWOOD_FENCE);
            registerDropSelf(BlockRegistry.STRIPPED_AWLOG_BLUE);
            registerDropSelf(BlockRegistry.STRIPPED_AWWOOD_BLUE);
            registerDropSelf(BlockRegistry.STRIPPED_AWLOG_GREEN);
            registerDropSelf(BlockRegistry.STRIPPED_AWWOOD_GREEN);
            registerDropSelf(BlockRegistry.STRIPPED_AWLOG_RED);
            registerDropSelf(BlockRegistry.STRIPPED_AWWOOD_RED);
            registerDropSelf(BlockRegistry.STRIPPED_AWLOG_PURPLE);
            registerDropSelf(BlockRegistry.STRIPPED_AWWOOD_PURPLE);
            registerDropDoor(BlockRegistry.ARCHWOOD_DOOR.get());
            registerDropSelf(BlockRegistry.SOURCE_GEM_BLOCK);

            registerDropSelf(BlockRegistry.POTION_MELDER);
            registerDropSelf(BlockRegistry.RITUAL_BLOCK);
            registerDropSelf(BlockRegistry.GOLD_SCONCE_BLOCK);
            registerBedCondition(BlockRegistry.SCRIBES_BLOCK.get(), ScribesBlock.PART, ThreePartBlock.HEAD);
            registerDrop(BlockRegistry.DRYGMY_BLOCK.get(), Items.MOSSY_COBBLESTONE);

            registerDropSelf(BlockRegistry.VITALIC_BLOCK);
            registerDropSelf(BlockRegistry.ALCHEMICAL_BLOCK);
            registerDropSelf(BlockRegistry.MYCELIAL_BLOCK);
            registerDropSelf(BlockRegistry.TIMER_SPELL_TURRET);
            registerDropSelf(BlockRegistry.BASIC_SPELL_TURRET);

            registerDropSelf(BlockRegistry.ARCHWOOD_CHEST);
            registerDropSelf(BlockRegistry.SPELL_PRISM);

            registerDropSelf(BlockRegistry.AGRONOMIC_SOURCELINK);
            registerDropSelf(BlockRegistry.ENCHANTING_APP_BLOCK);

            LootPool.Builder pedestal = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(BlockRegistry.ARCANE_PEDESTAL)
                            .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY)));
            add(BlockRegistry.ARCANE_PEDESTAL.get(), LootTable.lootTable().withPool(pedestal));

            LootPool.Builder platform = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(BlockRegistry.ARCANE_PLATFORM)
                            .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY)));
            add(BlockRegistry.ARCANE_PLATFORM.get(), LootTable.lootTable().withPool(platform));

            registerDropSelf(BlockRegistry.RELAY);
            registerDropSelf(BlockRegistry.RELAY_SPLITTER);
            registerDropSelf(BlockRegistry.ARCANE_CORE_BLOCK);
            registerDropSelf(BlockRegistry.IMBUEMENT_BLOCK);
            registerDropSelf(BlockRegistry.VOLCANIC_BLOCK);
            registerDropSelf(BlockRegistry.BRAZIER_RELAY);

            registerDropSelf(BlockRegistry.RELAY_WARP);
            registerDropSelf(BlockRegistry.RELAY_DEPOSIT);
            registerDropSelf(BlockRegistry.RELAY_COLLECTOR);
            registerDropSelf(BlockRegistry.CRAFTING_LECTERN.get());
            registerDropSelf(BlockRegistry.RED_SBED);
            registerDropSelf(BlockRegistry.YELLOW_SBED);
            registerDropSelf(BlockRegistry.GREEN_SBED);
            registerDropSelf(BlockRegistry.PURPLE_SBED);
            registerDropSelf(BlockRegistry.BLUE_SBED);
            registerDropSelf(BlockRegistry.ORANGE_SBED);
            registerDropSelf(BlockRegistry.SCRYERS_CRYSTAL);
            registerDropSelf(BlockRegistry.SCRYERS_OCULUS);
            registerDropSelf(BlockRegistry.POTION_DIFFUSER);
            for (String s : LibBlockNames.DECORATIVE_SOURCESTONE) {
                registerDropSelf(BlockRegistry.getBlock(s));
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ArsNouveau.MODID, s + "_stairs"));
                registerDropSelf(block);
                Block slab = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ArsNouveau.MODID, s + "_slab"));
                registerDropSelf(slab);

            }
            registerBedCondition(BlockRegistry.ALTERATION_TABLE.get(), AlterationTable.PART, ThreePartBlock.HEAD);
            registerDropSelf(BlockRegistry.VOID_PRISM);
            registerDropSelf(BlockRegistry.MAGEBLOOM_BLOCK);
            registerDropSelf(BlockRegistry.GHOST_WEAVE);
            registerDropSelf(BlockRegistry.FALSE_WEAVE);
            registerDropSelf(BlockRegistry.MIRROR_WEAVE);
            registerDropSelf(BlockRegistry.ITEM_DETECTOR);
            registerDropSelf(BlockRegistry.SKY_WEAVE);
            registerDropSelf(BlockRegistry.ROTATING_TURRET);
            registerDropSelf(BlockRegistry.SPELL_SENSOR);

            add(BlockRegistry.SOURCE_JAR.get(), createManaManchineTable(BlockRegistry.SOURCE_JAR.get()));

            LootPool.Builder potionJarBuilder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(BlockRegistry.POTION_JAR)
                            .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                            .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                    .copy("potionData", "BlockEntityTag.potionData", CopyNbtFunction.MergeStrategy.REPLACE)
                                    .copy("currentFill", "BlockEntityTag.currentFill", CopyNbtFunction.MergeStrategy.REPLACE)
                                    .copy("locked", "BlockEntityTag.locked", CopyNbtFunction.MergeStrategy.REPLACE)
                                    .copy("potionNames", "potionNames", CopyNbtFunction.MergeStrategy.REPLACE)
                                    .copy("currentFill", "fill", CopyNbtFunction.MergeStrategy.REPLACE))
                            .apply(SetContainerContents.setContents(BlockRegistry.POTION_JAR_TYPE.get())
                                    .withEntry(DynamicLoot.dynamicEntry(new ResourceLocation("minecraft", "contents"))))
                    );
            add(BlockRegistry.POTION_JAR.get(), LootTable.lootTable().withPool(potionJarBuilder));
            add(BlockRegistry.BASTION_POD.get(), LootTable.lootTable().withPool(POD_BUILDER(BlockRegistry.BASTION_POD.asItem(), BlockRegistry.BASTION_POD.get())));
            add(BlockRegistry.MENDOSTEEN_POD.get(), LootTable.lootTable().withPool(POD_BUILDER(BlockRegistry.MENDOSTEEN_POD.asItem(), BlockRegistry.MENDOSTEEN_POD.get())));
            add(BlockRegistry.FROSTAYA_POD.get(), LootTable.lootTable().withPool(POD_BUILDER(BlockRegistry.FROSTAYA_POD.asItem(), BlockRegistry.FROSTAYA_POD.get())));
            add(BlockRegistry.BOMBEGRANTE_POD.get(), LootTable.lootTable().withPool(POD_BUILDER(BlockRegistry.BOMBEGRANTE_POD.asItem(), BlockRegistry.BOMBEGRANTE_POD.get())));

            LootPool.Builder mobJarBuilder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(BlockRegistry.MOB_JAR)
                            .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                            .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                    .copy("entityTag", "BlockEntityTag.entityTag", CopyNbtFunction.MergeStrategy.REPLACE)
                                    .copy("entityId", "entityId", CopyNbtFunction.MergeStrategy.REPLACE))
                            .apply(SetContainerContents.setContents(BlockRegistry.MOB_JAR_TILE.get())
                                    .withEntry(DynamicLoot.dynamicEntry(new ResourceLocation("minecraft", "contents"))))
                    );
            add(BlockRegistry.MOB_JAR.get(), LootTable.lootTable().withPool(mobJarBuilder));
            //CustomName
            LootPool.Builder repository = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(BlockRegistry.REPOSITORY)
                            .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY)));
            add(BlockRegistry.REPOSITORY.get(), LootTable.lootTable().withPool(repository));
            registerDropSelf(BlockRegistry.SOURCESTONE_SCONCE_BLOCK.get());
            registerDropSelf(BlockRegistry.POLISHED_SCONCE_BLOCK.get());
            registerDropSelf(BlockRegistry.ARCHWOOD_SCONCE_BLOCK.get());
            registerDropSelf(BlockRegistry.REDSTONE_RELAY.get());
            registerDropSelf(BlockRegistry.SOURCEBERRY_SACK.get());
        }

        @Override
        protected void add(Block pBlock, LootTable.Builder pBuilder) {
            list.add(pBlock);
            super.add(pBlock, pBuilder);
        }

        @Override
        protected void add(Block pBlock, Function<Block, LootTable.Builder> pFactory) {
            list.add(pBlock);
            super.add(pBlock, pFactory);
        }


        protected void add(RegistryWrapper<Block> pBlock, LootTable.Builder pBuilder) {
            add(pBlock.get(), pBuilder);
        }


        protected void add(RegistryWrapper<Block> pBlock, Function<Block, LootTable.Builder> pFactory) {
            add(pBlock.get(), pFactory);
        }

        public LootPool.Builder POD_BUILDER(Item item, Block block) {
            return LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                    .add(LootItem.lootTableItem(item)
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(3.0F))
                                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ArchfruitPod.AGE, 2)))));
        }

        public LootTable.Builder createManaManchineTable(Block block) {
            LootPool.Builder builder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(block)
                            .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                            .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                    .copy("inv", "BlockEntityTag.inv", CopyNbtFunction.MergeStrategy.REPLACE) //addOperation
                                    .copy("source", "BlockEntityTag.source", CopyNbtFunction.MergeStrategy.REPLACE))
                            .apply(SetContainerContents.setContents(BlockRegistry.SOURCE_JAR_TILE.get())
                                    .withEntry(DynamicLoot.dynamicEntry(new ResourceLocation("minecraft", "contents"))))
                    );
            return LootTable.lootTable().withPool(builder);
        }

        protected void registerSlabItemTable(Block p_124291_) {
            list.add(p_124291_);
            this.add(p_124291_, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                    .add(applyExplosionDecay(p_124291_, LootItem.lootTableItem(p_124291_).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F))
                            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_124291_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.DOUBLE))))))));

        }

        // Override and ignore the missing loot table error
        @Override
        public void generate(BiConsumer<ResourceLocation, LootTable.Builder> p_249322_) {
            this.generate();
            Set<ResourceLocation> set = new HashSet<>();

            for (Block block : list) {
                if (block.isEnabled(this.enabledFeatures)) {
                    ResourceLocation resourcelocation = block.getLootTable();
                    if (resourcelocation != BuiltInLootTables.EMPTY && set.add(resourcelocation)) {
                        LootTable.Builder loottable$builder = this.map.remove(resourcelocation);
                        if (loottable$builder == null) {
                            continue;
                        }

                        p_249322_.accept(resourcelocation, loottable$builder);
                    }
                }
            }
        }

        protected <T extends Comparable<T> & StringRepresentable> void registerBedCondition(Block block, Property<T> prop, T isValue) {
            list.add(block);
            this.add(block, LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(block).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(prop, isValue)))))));
        }

        public void registerLeavesAndSticks(Block leaves, Block sapling) {
            list.add(leaves);
            this.add(leaves, l_state -> createLeavesDrops(l_state, sapling, DEFAULT_SAPLING_DROP_RATES));
        }

        public void registerDropDoor(Block block) {
            list.add(block);
            this.add(block, createDoorTable(block));
        }

        public void registerDropSelf(RegistryWrapper block) {
            list.add((Block) block.get());
            dropSelf((Block) block.get());
        }

        public void registerDropSelf(Block block) {
            list.add(block);
            dropSelf(block);
        }

        public void registerDropSelf(RegistryObject<Block> block) {
            list.add(block.get());
            dropSelf(block.get());
        }

        public void registerDrop(Block input, ItemLike output) {
            list.add(input);
            dropOther(input, output);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ForgeRegistries.BLOCKS.getValues().stream().filter(block -> ForgeRegistries.BLOCKS.getKey(block).getNamespace().equals(ArsNouveau.MODID)).collect(Collectors.toList());
        }

    }


    public static class EntityLootTable extends EntityLootSubProvider {
        private final Map<EntityType<?>, Map<ResourceLocation, LootTable.Builder>> map = Maps.newHashMap();

        protected EntityLootTable() {
            super(FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        public void generate() {
            add(ModEntities.WILDEN_STALKER.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemsRegistry.WILDEN_WING.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                    .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))))
            );
            add(ModEntities.WILDEN_GUARDIAN.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemsRegistry.WILDEN_SPIKE.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                    .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))))
            );
            add(ModEntities.WILDEN_HUNTER.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemsRegistry.WILDEN_HORN.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                    .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))))
            );
        }

        @Override
        protected void add(EntityType<?> pEntityType, LootTable.Builder pBuilder) {
            super.add(pEntityType, pBuilder);
            this.map.put(pEntityType, ImmutableMap.of(pEntityType.getDefaultLootTable(), pBuilder));
        }

        @Override
        protected void add(EntityType<?> pEntityType, ResourceLocation pLootTableLocation, LootTable.Builder pBuilder) {
            super.add(pEntityType, pLootTableLocation, pBuilder);
            this.map.computeIfAbsent(pEntityType, (p_249004_) -> {
                return Maps.newHashMap();
            }).put(pLootTableLocation, pBuilder);
        }

        @Override
        public void generate(BiConsumer<ResourceLocation, LootTable.Builder> p_251751_) {
            this.generate();
            Set<ResourceLocation> set = Sets.newHashSet();
            this.getKnownEntityTypes().map(EntityType::builtInRegistryHolder).forEach((p_249003_) -> {
                EntityType<?> entitytype = p_249003_.value();
                if (canHaveLootTable(entitytype)) {
                    Map<ResourceLocation, LootTable.Builder> map = this.map.remove(entitytype);
                    ResourceLocation resourcelocation = entitytype.getDefaultLootTable();
                    if (map != null) {
                        map.forEach((p_250376_, p_250972_) -> {
                            if (!set.add(p_250376_)) {
                                throw new IllegalStateException(String.format(Locale.ROOT, "Duplicate loottable '%s' for '%s'", p_250376_, p_249003_.key().location()));
                            } else {
                                p_251751_.accept(p_250376_, p_250972_);
                            }
                        });
                    }
                } else {
                    Map<ResourceLocation, LootTable.Builder> map1 = this.map.remove(entitytype);
                    if (map1 != null) {
                        throw new IllegalStateException(String.format(Locale.ROOT, "Weird loottables '%s' for '%s', not a LivingEntity so should not have loot", map1.keySet().stream().map(ResourceLocation::toString).collect(Collectors.joining(",")), p_249003_.key().location()));
                    }
                }

            });
        }

        @Override
        protected Stream<EntityType<?>> getKnownEntityTypes() {
            return ForgeRegistries.ENTITY_TYPES.getValues().stream().filter(block -> ForgeRegistries.ENTITY_TYPES.getKey(block).getNamespace().equals(ArsNouveau.MODID)).toList().stream();
        }
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {

    }
}
