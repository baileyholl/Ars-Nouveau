package com.hollingsworth.arsnouveau.common.datagen;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.*;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.setup.registry.*;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultTableProvider extends LootTableProvider {

    public DefaultTableProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(pOutput, new HashSet<>(), List.of(new LootTableProvider.SubProviderEntry(MyLootTableSubProvider::new, LootContextParamSets.ENTITY), new LootTableProvider.SubProviderEntry(BlockLootTable::new, LootContextParamSets.BLOCK), new LootTableProvider.SubProviderEntry(EntityLootTable::new, LootContextParamSets.ENTITY)), pRegistries);
    }

    public static class MyLootTableSubProvider implements LootTableSubProvider {
        public List<Block> list = new ArrayList<>();
        HolderLookup.Provider lookupProvider;

        public MyLootTableSubProvider(HolderLookup.Provider lookupProvider) {
            this.lookupProvider = lookupProvider;
        }

        @Override
        public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
            consumer.accept(ResourceKey.create(Registries.LOOT_TABLE, ArsNouveau.prefix("dispel_witch")), LootTable.lootTable()
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                    .withPool(LootPool.lootPool()
                            .add(LootItem.lootTableItem(ItemsRegistry.WIXIE_SHARD))
                            .setRolls(ConstantValue.exactly(1))));
        }
    }

    private static final float[] DEFAULT_SAPLING_DROP_RATES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};

    public static class BlockLootTable extends BlockLootSubProvider {
        public List<Block> list = new ArrayList<>();

        protected BlockLootTable(HolderLookup.Provider pRegistries) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), pRegistries);
        }

        @Override
        protected void generate() {
            HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
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
            registerDropSelf(BlockRegistry.ARCHWOOD_SIGN);
            registerDropSelf(BlockRegistry.ARCHWOOD_HANGING_SIGN);
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
                Block block = BuiltInRegistries.BLOCK.get(ArsNouveau.prefix(s + "_stairs"));
                registerDropSelf(block);
                Block slab = BuiltInRegistries.BLOCK.get(ArsNouveau.prefix(s + "_slab"));
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
            registerDropSelf(BlockRegistry.CREATIVE_SOURCE_JAR);

            add(BlockRegistry.SOURCE_JAR.get(), createManaManchineTable(BlockRegistry.SOURCE_JAR.get()));

            LootPool.Builder potionJarBuilder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(BlockRegistry.POTION_JAR)
                            .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                            .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY).include(DataComponentRegistry.POTION_JAR.get()))
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
                            .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY).include(DataComponentRegistry.MOB_JAR.get()))
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
            LootItemCondition.Builder lootitemcondition$builder1 = LootItemBlockStatePropertyCondition.hasBlockStateProperties(BlockRegistry.MAGE_BLOOM_CROP.get())
                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE, 7));
            add(BlockRegistry.MAGE_BLOOM_CROP.get(), createCropDrops(BlockRegistry.MAGE_BLOOM_CROP.get(), ItemsRegistry.MAGE_BLOOM.asItem(), BlockRegistry.MAGE_BLOOM_CROP.get().asItem(), lootitemcondition$builder1, 0));

            this.add(
                    BlockRegistry.SOURCEBERRY_BUSH.get(),
                    p_249159_ -> this.applyExplosionDecay(
                            p_249159_,
                            LootTable.lootTable()
                                    .withPool(
                                            LootPool.lootPool()
                                                    .when(
                                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(BlockRegistry.SOURCEBERRY_BUSH.get())
                                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SourceBerryBush.AGE, 3))
                                                    )
                                                    .add(LootItem.lootTableItem(BlockRegistry.SOURCEBERRY_BUSH.asItem()))
                                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F)))
                                                    .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                                    )
                                    .withPool(
                                            LootPool.lootPool()
                                                    .when(
                                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(BlockRegistry.SOURCEBERRY_BUSH.get())
                                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SourceBerryBush.AGE, 2))
                                                    )
                                                    .add(LootItem.lootTableItem(BlockRegistry.SOURCEBERRY_BUSH.asItem()))
                                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                                    .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                                    )
                    )
            );
            registerDropSelf(BlockRegistry.ARCHWOOD_GRATE);
            registerDropSelf(BlockRegistry.GOLD_GRATE);
            registerDropSelf(BlockRegistry.SMOOTH_SOURCESTONE_GRATE);
            registerDropSelf(BlockRegistry.SOURCESTONE_GRATE);
            registerDropSelf(BlockRegistry.SOURCE_LAMP);
            registerDropSelf(BlockRegistry.REPOSITORY_CONTROLLER);
            LootPool.Builder dimBlockBuilder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(BlockRegistry.PLANARIUM)
                            .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                    );
            add(BlockRegistry.PLANARIUM.get(), LootTable.lootTable().withPool(dimBlockBuilder));
            registerDropSelf(BlockRegistry.DECOR_BLOSSOM);
        }

        protected LootTable.Builder createCropDrops(Block pCropBlock, Item pGrownCropItem, Item pSeedsItem, LootItemCondition.Builder pDropGrownCropCondition, int bonus) {
            HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
            return this.applyExplosionDecay(
                    pCropBlock,
                    LootTable.lootTable()
                            .withPool(LootPool.lootPool().add(LootItem.lootTableItem(pGrownCropItem).when(pDropGrownCropCondition).otherwise(LootItem.lootTableItem(pSeedsItem))))
                            .withPool(
                                    LootPool.lootPool()
                                            .when(pDropGrownCropCondition)
                                            .add(
                                                    LootItem.lootTableItem(pSeedsItem)
                                                            .apply(ApplyBonusCount.addBonusBinomialDistributionCount(registrylookup.getOrThrow(Enchantments.FORTUNE), 0.5714286F, bonus))
                                            )
                            )
            );
        }

        @Override
        protected void add(@NotNull Block pBlock, LootTable.@NotNull Builder pBuilder) {
            list.add(pBlock);
            super.add(pBlock, pBuilder);
        }

        @Override
        protected void add(@NotNull Block pBlock, @NotNull Function<Block, LootTable.Builder> pFactory) {
            list.add(pBlock);
            super.add(pBlock, pFactory);
        }


        protected void add(RegistryWrapper<Block, Block> pBlock, LootTable.Builder pBuilder) {
            add(pBlock.get(), pBuilder);
        }


        protected void add(RegistryWrapper<Block, Block> pBlock, Function<Block, LootTable.Builder> pFactory) {
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
                            .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY).include(DataComponentRegistry.BLOCK_FILL_CONTENTS.get()))
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
        public void generate(@NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> pGenerator) {
            this.generate();
            Set<ResourceKey<LootTable>> set = new HashSet<>();

            for (Block block : list) {
                if (block.isEnabled(this.enabledFeatures)) {
                    ResourceKey<LootTable> resourcelocation = block.getLootTable();
                    if (resourcelocation != BuiltInLootTables.EMPTY && set.add(resourcelocation)) {
                        LootTable.Builder loottable$builder = this.map.remove(resourcelocation);
                        if (loottable$builder == null) {
                            continue;
                        }

                        pGenerator.accept(resourcelocation, loottable$builder);
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

        private <T extends Block> void registerDropSelf(BlockRegistryWrapper<T> block) {
            registerDropSelf(block.get());
        }

        public void registerDropSelf(Block block) {
            list.add(block);
            dropSelf(block);
        }

        public void registerDrop(Block input, ItemLike output) {
            list.add(input);
            dropOther(input, output);
        }

        @Override
        protected @NotNull Iterable<Block> getKnownBlocks() {
            return BuiltInRegistries.BLOCK.stream().filter(block -> BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(ArsNouveau.MODID)).collect(Collectors.toList());
        }

    }


    public static class EntityLootTable extends EntityLootSubProvider {
        private final Map<EntityType<?>, Map<ResourceKey<LootTable>, LootTable.Builder>> map = Maps.newHashMap();

        protected EntityLootTable(HolderLookup.Provider pRegistries) {
            super(FeatureFlags.REGISTRY.allFlags(), pRegistries);
        }

        @Override
        public void generate() {
            add(ModEntities.WILDEN_STALKER.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemsRegistry.WILDEN_WING.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                    .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))))
            );
            add(ModEntities.WILDEN_GUARDIAN.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemsRegistry.WILDEN_SPIKE.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                    .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))))
            );
            add(ModEntities.WILDEN_HUNTER.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemsRegistry.WILDEN_HORN.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                    .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))))
            );
        }

        @Override
        protected void add(@NotNull EntityType<?> pEntityType, LootTable.@NotNull Builder pBuilder) {
            super.add(pEntityType, pBuilder);
            this.map.put(pEntityType, ImmutableMap.of(pEntityType.getDefaultLootTable(), pBuilder));
        }

        @Override
        protected void add(@NotNull EntityType<?> pEntityType, @NotNull ResourceKey<LootTable> pLootTableLocation, LootTable.@NotNull Builder pBuilder) {
            super.add(pEntityType, pLootTableLocation, pBuilder);
            this.map.computeIfAbsent(pEntityType, (p_249004_) -> {
                return Maps.newHashMap();
            }).put(pLootTableLocation, pBuilder);
        }

        @Override
        public void generate(@NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> pGenerator) {
            this.generate();
            Set<ResourceKey<LootTable>> set = Sets.newHashSet();
            this.getKnownEntityTypes().map(EntityType::builtInRegistryHolder).forEach((p_249003_) -> {
                EntityType<?> entitytype = p_249003_.value();
                if (canHaveLootTable(entitytype)) {
                    Map<ResourceKey<LootTable>, LootTable.Builder> map = this.map.remove(entitytype);
                    ResourceKey<LootTable> resourcelocation = entitytype.getDefaultLootTable();
                    if (map != null) {
                        map.forEach((p_250376_, p_250972_) -> {
                            if (!set.add(p_250376_)) {
                                throw new IllegalStateException(String.format(Locale.ROOT, "Duplicate loottable '%s' for '%s'", p_250376_, p_249003_.key().location()));
                            } else {
                                pGenerator.accept(p_250376_, p_250972_);
                            }
                        });
                    }
                } else {
                    Map<ResourceKey<LootTable>, LootTable.Builder> map1 = this.map.remove(entitytype);
                    if (map1 != null) {
                        throw new IllegalStateException(String.format(Locale.ROOT, "Weird loottables '%s' for '%s', not a LivingEntity so should not have loot", map1.keySet().stream().map(ResourceKey::toString).collect(Collectors.joining(",")), p_249003_.key().location()));
                    }
                }

            });
        }

        @Override
        protected @NotNull Stream<EntityType<?>> getKnownEntityTypes() {
            return BuiltInRegistries.ENTITY_TYPE.stream().filter(block -> BuiltInRegistries.ENTITY_TYPE.getKey(block).getNamespace().equals(ArsNouveau.MODID)).toList().stream();
        }
    }

    @Override
    protected void validate(@NotNull WritableRegistry<LootTable> writableregistry, @NotNull ValidationContext validationcontext, ProblemReporter.@NotNull Collector problemreporter$collector) {

    }
}
