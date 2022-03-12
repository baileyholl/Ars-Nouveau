package com.hollingsworth.arsnouveau.common.world;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.SourceBerryBush;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.world.tree.MagicTrunkPlacer;
import com.hollingsworth.arsnouveau.common.world.tree.SupplierBlockStateProvider;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

//package com.hollingsworth.arsnouveau.common.world;
//
//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.ImmutableSet;
//import com.hollingsworth.arsnouveau.ArsNouveau;
//import com.hollingsworth.arsnouveau.common.block.tile.LightTile;
//import com.hollingsworth.arsnouveau.common.entity.ModEntities;
//import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
//import com.hollingsworth.arsnouveau.common.world.feature.SingleBlockFeature;
//import com.hollingsworth.arsnouveau.common.world.tree.MagicTrunkPlacer;
//import com.hollingsworth.arsnouveau.common.world.tree.SupplierBlockStateProvider;
//import com.hollingsworth.arsnouveau.setup.BlockRegistry;
//import com.hollingsworth.arsnouveau.setup.Config;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Registry;
//import net.minecraft.data.BuiltinRegistries;
//import net.minecraft.data.worldgen.biome.VanillaBiomes;
//import net.minecraft.resources.ResourceKey;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.server.level.WorldGenRegion;
//import net.minecraft.tags.BlockTags;
//import net.minecraft.util.UniformInt;
//import net.minecraft.world.entity.MobCategory;
//import net.minecraft.world.gen.feature.*;
//import net.minecraft.world.gen.feature.template.*;
//import net.minecraft.world.level.WorldGenLevel;
//import net.minecraft.world.level.biome.Biome;
//import net.minecraft.world.level.biome.MobSpawnSettings;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.IronBarsBlock;
//import net.minecraft.world.level.chunk.ChunkGenerator;
//import net.minecraft.world.level.levelgen.GenerationStep;
//import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
//import net.minecraft.world.level.levelgen.feature.Feature;
//import net.minecraft.world.level.levelgen.feature.blockplacers.SimpleBlockPlacer;
//import net.minecraft.world.level.levelgen.feature.configurations.*;
//import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
//import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
//import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
//import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
//import net.minecraft.world.level.levelgen.placement.FrequencyWithExtraChanceDecoratorConfiguration;
//import net.minecraft.world.level.levelgen.structure.templatesystem.*;
//import net.minecraftforge.common.BiomeDictionary;
//import net.minecraftforge.event.RegistryEvent;
//import net.minecraftforge.event.world.BiomeLoadingEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.registries.ObjectHolder;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Objects;
//import java.util.Random;
//
//import static com.hollingsworth.arsnouveau.common.world.FeatureLib.*;
//import static net.minecraft.data.worldgen.Features.*;
//
@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class WorldEvent {
    public static ConfiguredFeature<TreeConfiguration, ?> CASCADING_TREE =  Feature.TREE.configured((
            new TreeConfiguration.TreeConfigurationBuilder(new SupplierBlockStateProvider(LibBlockNames.CASCADING_LOG),
                    new MagicTrunkPlacer(9, 1, 0),
                    new SupplierBlockStateProvider(LibBlockNames.CASCADING_LEAVES),
                    new BlobFoliagePlacer(UniformInt.of(0, 0), UniformInt.of(0,0), 0),
                    new TwoLayersFeatureSize(2, 0, 2))).ignoreVines().build());



    public static ConfiguredFeature<TreeConfiguration, ?> BLAZING_TREE =  Feature.TREE.configured((
            new TreeConfiguration.TreeConfigurationBuilder(new SupplierBlockStateProvider(LibBlockNames.BLAZING_LOG),
                    new MagicTrunkPlacer(9, 1, 0),
                    new SupplierBlockStateProvider(LibBlockNames.BLAZING_LEAVES),
                    new BlobFoliagePlacer(UniformInt.of(0, 0), UniformInt.of(0, 0), 0),
                    new TwoLayersFeatureSize(2, 0, 2))).ignoreVines().build());

    public static ConfiguredFeature<TreeConfiguration, ?> FLOURISHING_TREE =  Feature.TREE.configured((
            new TreeConfiguration.TreeConfigurationBuilder(new SupplierBlockStateProvider(LibBlockNames.FLOURISHING_LOG),
                    new MagicTrunkPlacer(9, 1, 0),
                    new SupplierBlockStateProvider(LibBlockNames.FLOURISHING_LEAVES),
                    new BlobFoliagePlacer(UniformInt.of(0, 0), UniformInt.of(0, 0), 0),
                    new TwoLayersFeatureSize(2, 0, 2))).ignoreVines().build());

    public static ConfiguredFeature<TreeConfiguration, ?> VEXING_TREE =  Feature.TREE.configured((
            new TreeConfiguration.TreeConfigurationBuilder(new SupplierBlockStateProvider(LibBlockNames.VEXING_LOG),
                    new MagicTrunkPlacer(9, 1, 0),
                    new SupplierBlockStateProvider(LibBlockNames.VEXING_LEAVES),
                    new BlobFoliagePlacer(UniformInt.of(0, 0), UniformInt.of(0, 0), 0),
                    new TwoLayersFeatureSize(2, 0, 2))).ignoreVines().build());
//
//
//    public static ConfiguredFeature<TreeConfiguration, ?> MAGIC_TREE_CONFIG2 = Feature.TREE.configured((
//            new TreeConfiguration.TreeConfigurationBuilder(new SimpleStateProvider(Blocks.DARK_OAK_LOG.defaultBlockState()),
//                    new SimpleStateProvider(Blocks.DARK_OAK_LEAVES.defaultBlockState()),
//                    new BlobFoliagePlacer(UniformInt.fixed(0), UniformInt.fixed(0), 0),
//                    new MagicTrunkPlacer(9, 1, 0),
//                    new TwoLayersFeatureSize(2, 0, 2))).ignoreVines().build());
//
//    public static final StructureProcessorList ARCHWOOD_PLAINS =
//            new StructureProcessorList(ImmutableList.of(new RuleProcessor(
//                    ImmutableList.of(new ProcessorRule(new RandomBlockMatchTest(Blocks.COBBLESTONE, 0.8F), AlwaysTrueTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.defaultBlockState()),
//                            new ProcessorRule(new TagMatchTest(BlockTags.DOORS), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()),
//                            new ProcessorRule(new BlockMatchTest(Blocks.TORCH), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()),
//                            new ProcessorRule(new BlockMatchTest(Blocks.WALL_TORCH), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()),
//                            new ProcessorRule(new RandomBlockMatchTest(Blocks.COBBLESTONE, 0.07F), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()),
//                            new ProcessorRule(new RandomBlockMatchTest(Blocks.MOSSY_COBBLESTONE, 0.07F), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()),
//                            new ProcessorRule(new RandomBlockMatchTest(Blocks.WHITE_TERRACOTTA, 0.07F), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()),
//                            new ProcessorRule(new RandomBlockMatchTest(Blocks.OAK_LOG, 0.05F), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()),
//                            new ProcessorRule(new RandomBlockMatchTest(Blocks.OAK_PLANKS, 0.1F), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()),
//                            new ProcessorRule(new RandomBlockMatchTest(Blocks.OAK_STAIRS, 0.1F), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()),
//                            new ProcessorRule(new RandomBlockMatchTest(Blocks.STRIPPED_OAK_LOG, 0.02F), AlwaysTrueTest.INSTANCE, Blocks.COBWEB.defaultBlockState()),
//                            new ProcessorRule(new BlockStateMatchTest(Blocks.GLASS_PANE.defaultBlockState().setValue(IronBarsBlock.NORTH, Boolean.TRUE).setValue(IronBarsBlock.SOUTH, Boolean.TRUE)), AlwaysTrueTest.INSTANCE, Blocks.BROWN_STAINED_GLASS_PANE.defaultBlockState().setValue(IronBarsBlock.NORTH, Boolean.TRUE).setValue(IronBarsBlock.SOUTH, Boolean.TRUE)),
//                            new ProcessorRule(new BlockStateMatchTest(Blocks.GLASS_PANE.defaultBlockState().setValue(IronBarsBlock.EAST, Boolean.TRUE).setValue(IronBarsBlock.WEST, Boolean.TRUE)), AlwaysTrueTest.INSTANCE, Blocks.BROWN_STAINED_GLASS_PANE.defaultBlockState().setValue(IronBarsBlock.EAST, Boolean.TRUE).setValue(IronBarsBlock.WEST, Boolean.TRUE)),
//                            new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.3F), AlwaysTrueTest.INSTANCE, Blocks.CARROTS.defaultBlockState()), new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.2F), AlwaysTrueTest.INSTANCE, Blocks.POTATOES.defaultBlockState()), new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.1F), AlwaysTrueTest.INSTANCE, Blocks.BEETROOTS.defaultBlockState())))));
//
//    public static final ResourceLocation EXTRA_ARCANE_ORE = new ResourceLocation(ArsNouveau.MODID, "arcane_ore_extra");
//    public static final Feature<BlockStateConfiguration> LIGHTS = new SingleBlockFeature(BlockStateConfiguration.CODEC) {
//
//        public void onStatePlace(WorldGenLevel seed, ChunkGenerator chunkGenerator, Random rand, BlockPos pos, BlockStateConfiguration config) {
//            if(seed instanceof WorldGenRegion){
//                WorldGenRegion world = (WorldGenRegion) seed;
//                Random random = world.getRandom();
//                if(world.getBlockEntity(pos) instanceof LightTile){
//                    LightTile tile = (LightTile) world.getBlockEntity(pos);
//                    tile.red = Math.max(10, random.nextInt(255));
//                    tile.green = Math.max(10, random.nextInt(255));
//                    tile.blue = Math.max(10, random.nextInt(255));
//                }
//            }
//        }
//    };

    public static void registerFeatures() {
        ConfiguredFeature<?, ?> PATCH_BERRY_BUSH = FeatureUtils.register("ars_nouveau:patch_berry", Feature.RANDOM_PATCH.configured(FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK.configured(new SimpleBlockConfiguration(BlockStateProvider.simple(BlockRegistry.SOURCEBERRY_BUSH.defaultBlockState().setValue(SourceBerryBush.AGE, 3)))), List.of(Blocks.GRASS_BLOCK))));

        PlacedFeature BERRY_BUSH_PATCH_CONFIG = PlacementUtils.register("ars_nouveau:placed_berry", PATCH_BERRY_BUSH.placed(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        float treeChance = Config.TREE_SPAWN_RATE.get().floatValue();

        PlacedFeature PLACED_CASCADE = PlacementUtils.register("ars_nouveau:placed_cascade", CASCADING_TREE.filteredByBlockSurvival(BlockRegistry.CASCADING_SAPLING));
        PlacedFeature PLACED_BLAZING = PlacementUtils.register("ars_nouveau:placed_blazing", BLAZING_TREE.filteredByBlockSurvival(BlockRegistry.BLAZING_SAPLING));
        PlacedFeature PLACED_VEXING = PlacementUtils.register("ars_nouveau:placed_vexing", VEXING_TREE.filteredByBlockSurvival(BlockRegistry.VEXING_SAPLING));
        PlacedFeature PLACED_FLOURISHING = PlacementUtils.register("ars_nouveau:placed_flourishing", FLOURISHING_TREE.filteredByBlockSurvival(BlockRegistry.FLOURISHING_SAPLING));


        ConfiguredFeature<RandomFeatureConfiguration, ?> CONFIGURED = FeatureUtils.register("ars_nouveau:random_cascade", Feature.RANDOM_SELECTOR.configured(new RandomFeatureConfiguration(List.of(
                new WeightedPlacedFeature(PLACED_CASCADE, 0.25f),
                new WeightedPlacedFeature(PLACED_BLAZING, 0.25f),
                new WeightedPlacedFeature(PLACED_VEXING, 0.25f),
                new WeightedPlacedFeature(PLACED_FLOURISHING, 0.25f)), PLACED_CASCADE)));
        if(Config.TREE_SPAWN_RATE.get() > 0)
            PlacementUtils.register("ars_nouveau:archwood",CONFIGURED.placed(VegetationPlacements.treePlacement(RarityFilter.onAverageOnceEvery(Config.TREE_SPAWN_RATE.get()))));
//

        //
//        ConfiguredFeature<?, ?> CASCADE = CASCADING_TREE
//                .decorated(Features.Decorators.HEIGHTMAP_SQUARE)
//                .decorated(FeatureDecorator.COUNT_EXTRA.configured(new FrequencyWithExtraChanceDecoratorConfiguration(0, treeChance, 1)));
//        Feature.TREE.configured()

        //        ConfiguredFeature<?, ?> BLAZE = BLAZING_TREE
//                .decorated(Features.Decorators.HEIGHTMAP_SQUARE)
//                .decorated(FeatureDecorator.COUNT_EXTRA.configured(new FrequencyWithExtraChanceDecoratorConfiguration(0, treeChance, 1)));
//        ConfiguredFeature<?, ?> VEX = VEXING_TREE
//                .decorated(Features.Decorators.HEIGHTMAP_SQUARE)
//                .decorated(FeatureDecorator.COUNT_EXTRA.configured(new FrequencyWithExtraChanceDecoratorConfiguration(0, treeChance, 1)));
//        ConfiguredFeature<?, ?> FLOURISHING = FLOURISHING_TREE
//                .decorated(Features.Decorators.HEIGHTMAP_SQUARE)
//                .decorated(FeatureDecorator.COUNT_EXTRA.configured(new FrequencyWithExtraChanceDecoratorConfiguration(0, treeChance, 1)));
//
//
//        ConfiguredFeature<?, ?> FLOURISHING_COMMON = FLOURISHING_TREE
//                .decorated(Features.Decorators.HEIGHTMAP_SQUARE)
//                .decorated(FeatureDecorator.COUNT_EXTRA.configured(new FrequencyWithExtraChanceDecoratorConfiguration(12, 0.01f, 1)));
//        ConfiguredFeature<?, ?> CASCADE_COMMON = CASCADING_TREE
//                .decorated(Features.Decorators.HEIGHTMAP_SQUARE)
//                .decorated(FeatureDecorator.COUNT_EXTRA.configured(new FrequencyWithExtraChanceDecoratorConfiguration(12, 0.01f, 1)));
//        ConfiguredFeature<?, ?> BLAZE_COMMON = BLAZING_TREE
//                .decorated(Features.Decorators.HEIGHTMAP_SQUARE)
//                .decorated(FeatureDecorator.COUNT_EXTRA.configured(new FrequencyWithExtraChanceDecoratorConfiguration(12, 0.01f, 1)));
//        ConfiguredFeature<?, ?> VEX_COMMON = VEXING_TREE
//                .decorated(Features.Decorators.HEIGHTMAP_SQUARE)
//                .decorated(FeatureDecorator.COUNT_EXTRA.configured(new FrequencyWithExtraChanceDecoratorConfiguration(12, 0.01f, 1)));
//
//        ConfiguredFeature<?, ?> BLAZE_SEMI = BLAZING_TREE
//                .decorated(Features.Decorators.HEIGHTMAP_SQUARE)
//                .decorated(FeatureDecorator.COUNT_EXTRA.configured(new FrequencyWithExtraChanceDecoratorConfiguration(1, 0.01f, 1)));
//
//
//
//
//        ConfiguredFeature<?, ?> RANDOM_LIGHTS = LIGHTS.configured(new BlockStateConfiguration(BlockRegistry.LIGHT_BLOCK.defaultBlockState()))
//                .decorated(Features.Decorators.HEIGHTMAP_SQUARE)
//                .decorated(FeatureDecorator.COUNT_EXTRA.configured(new FrequencyWithExtraChanceDecoratorConfiguration(0, 0.5f, 1)));
//
//
//
//        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, BlockRegistry.ARCANE_ORE.getRegistryName(), Feature.ORE.configured(new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE,
//                BlockRegistry.ARCANE_ORE.defaultBlockState(), 5)).range(60).squared().count(5));
//        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, EXTRA_ARCANE_ORE, Feature.ORE.configured(new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, BlockRegistry.ARCANE_ORE.defaultBlockState(), 9))
//                .decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(32, 32, 80))).squared().count(20));
//
//        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, BlockRegistry.MANA_BERRY_BUSH.getRegistryName(),
//                Feature.RANDOM_PATCH.configured(BERRY_BUSH_PATCH_CONFIG).decorated(Features.Decorators.HEIGHTMAP_DOUBLE_SQUARE));
//
//        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, BlockRegistry.VEXING_SAPLING.getRegistryName(), VEX);
//        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, BlockRegistry.BLAZING_SAPLING.getRegistryName(), BLAZE);
        //Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, BlockRegistry.CASCADING_SAPLING.getRegistryName(), CASCADE);
//        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, BlockRegistry.FLOURISHING_SAPLING.getRegistryName(), FLOURISHING);
//        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, FeatureLib.RANDOM_LIGHTS_LOC, RANDOM_LIGHTS);
//
//        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, FeatureLib.BLAZE_COMMON_LOC, BLAZE_COMMON);
//        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, FeatureLib.BLAZE_SEMI_LOC, BLAZE_SEMI);
//
//        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, ARCHWOOD_TREES, Feature.RANDOM_SELECTOR.configured(new RandomFeatureConfiguration(ImmutableList.of(
//                VEX_COMMON.weighted(0.15f),
//                BLAZE_COMMON.weighted(0.15f),
//                CASCADE_COMMON.weighted(0.15f),
//                FLOURISHING_COMMON.weighted(0.15f)
//                ),VEXING_TREE
//                .decorated(Features.Decorators.HEIGHTMAP_SQUARE)
//                .decorated(FeatureDecorator.COUNT_EXTRA.configured(new FrequencyWithExtraChanceDecoratorConfiguration(0, 0, 0))))));
//
//        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, VANILLA_BIG_TREES, Feature.RANDOM_SELECTOR.configured(new RandomFeatureConfiguration(ImmutableList.of(
//                FANCY_OAK.weighted(0.5f),
//                FANCY_OAK_BEES_0002.weighted(0.2f),
//                FANCY_OAK_BEES_005.weighted(0.2f),
//                DARK_OAK.weighted(0.1f)
//        ),VEXING_TREE
//                .decorated(Features.Decorators.HEIGHTMAP_SQUARE)
//                .decorated(FeatureDecorator.COUNT_EXTRA.configured(new FrequencyWithExtraChanceDecoratorConfiguration(0, 0, 0))))));


       // Registry.register(WorldGenRegistries.PROCESSOR_LIST, new ResourceLocation(ArsNouveau.MODID, "archwood_plains"), ARCHWOOD_PLAINS);

    }

    @SubscribeEvent
    public static void biomeLoad(BiomeLoadingEvent e) {

        if (e.getCategory() == Biome.BiomeCategory.NETHER || e.getCategory() == Biome.BiomeCategory.THEEND)
            return;

        addMobSpawns(e);


        if ((e.getCategory().equals(Biome.BiomeCategory.TAIGA) || e.getName().equals(new ResourceLocation(ArsNouveau.MODID, "archwood_forest")))  && Config.SPAWN_BERRIES.get()) {
            e.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Objects.requireNonNull(BuiltinRegistries.PLACED_FEATURE.get(new ResourceLocation("ars_nouveau:placed_berry")))).build();
        }

        //Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(MAGIC_TREE_CONFIG.withChance(0.2F)), MAGIC_TREE_CONFIG)),
//        e.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
//                Objects.requireNonNull(BuiltinRegistries.CONFIGURED_FEATURE.get(BlockRegistry.VEXING_SAPLING.getRegistryName()))).build();
        if(Config.TREE_SPAWN_RATE.get() > 0)
            e.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Objects.requireNonNull(BuiltinRegistries.PLACED_FEATURE.get(new ResourceLocation(ArsNouveau.MODID, "archwood"))));

//        e.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
//                Objects.requireNonNull(BuiltinRegistries.CONFIGURED_FEATURE.get(BlockRegistry.BLAZING_SAPLING.getRegistryName()))).build();
//
//        e.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
//                Objects.requireNonNull(BuiltinRegistries.CONFIGURED_FEATURE.get(BlockRegistry.FLOURISHING_SAPLING.getRegistryName()))).build();

//        if(e.getName().equals(archwoodForest.getRegistryName())){
//            addArchwoodForestFeatures(e);
//        }
    }
//
    public static void addMobSpawns(BiomeLoadingEvent e){
        List<Biome.BiomeCategory> categories = Arrays.asList(Biome.BiomeCategory.FOREST, Biome.BiomeCategory.EXTREME_HILLS, Biome.BiomeCategory.JUNGLE, Biome.BiomeCategory.PLAINS, Biome.BiomeCategory.SWAMP, Biome.BiomeCategory.SAVANNA, Biome.BiomeCategory.MOUNTAIN);

        if (categories.contains(e.getCategory())) {
            if (Config.CARBUNCLE_WEIGHT.get() > 0) {
                e.getSpawns().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.STARBUNCLE_TYPE, Config.CARBUNCLE_WEIGHT.get(), 1, 1));
            }
            if (Config.SYLPH_WEIGHT.get() > 0) {
                e.getSpawns().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.WHIRLISPRIG_TYPE, Config.SYLPH_WEIGHT.get(), 1, 1));
            }
        }
        if (Config.DRYGMY_WEIGHT.get() > 0) {
            e.getSpawns().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.ENTITY_DRYGMY, Config.DRYGMY_WEIGHT.get(), 1, 1));
        }

        if(!e.getCategory().equals(Biome.BiomeCategory.MUSHROOM) && !e.getCategory().equals(Biome.BiomeCategory.NONE)){
            if(e.getClimate().temperature <= 0.35f &&  Config.WGUARDIAN_WEIGHT.get() > 0){
                e.getSpawns().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.WILDEN_GUARDIAN, Config.WGUARDIAN_WEIGHT.get(), 1, 1));
            }
            if( Config.WSTALKER_WEIGHT.get() > 0)
                e.getSpawns().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.WILDEN_STALKER, Config.WSTALKER_WEIGHT.get(), 3, 3));
            if( Config.WHUNTER_WEIGHT.get() > 0)
                e.getSpawns().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.WILDEN_HUNTER, Config.WHUNTER_WEIGHT.get(), 1, 1));
        }
    }
//
//    public static void addBlazingForestFeatures(BiomeLoadingEvent e){
//        e.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
//                Objects.requireNonNull(BuiltinRegistries.CONFIGURED_FEATURE.get(BLAZE_SEMI_LOC))).build();
//
//    }
//
//    public static void addArchwoodForestFeatures(BiomeLoadingEvent e){
//        e.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
//                Objects.requireNonNull(BuiltinRegistries.CONFIGURED_FEATURE.get(ARCHWOOD_TREES))).build();
//        e.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
//                Objects.requireNonNull(BuiltinRegistries.CONFIGURED_FEATURE.get(VANILLA_BIG_TREES))).build();
//        e.getGeneration().addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS,
//                Objects.requireNonNull(BuiltinRegistries.CONFIGURED_FEATURE.get(RANDOM_LIGHTS_LOC))).build();
//        if (Config.SPAWN_ORE.get()) {
//            e.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES,
//                    Objects.requireNonNull(BuiltinRegistries.CONFIGURED_FEATURE.get(EXTRA_ARCANE_ORE))).build();
//        }
//    }
//
//    public static Biome archwoodForest = VanillaBiomes.theVoidBiome().setRegistryName(ArsNouveau.MODID, "archwood_forest");
//    public static Biome blazingForest = VanillaBiomes.theVoidBiome().setRegistryName(ArsNouveau.MODID, "blazing_archwood_forest");
//    public static ResourceKey<Biome> archwoodKey = BiomeRegistry.k(archwoodForest);
//
//    @ObjectHolder(ArsNouveau.MODID)
//    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
//    public static class BiomeRegistry{
//        @SubscribeEvent
//        public static void biomeRegistry(final RegistryEvent.Register<Biome> biomeRegistryEvent) {
//            biomeRegistryEvent.getRegistry().registerAll(archwoodForest);
//            BiomeDictionary.addTypes(archwoodKey, BiomeDictionary.Type.OVERWORLD);
//        }
//        @SubscribeEvent
//        public static void featureRegistry(final RegistryEvent.Register<Feature<?>> registryEvent) {
//            registryEvent.getRegistry().register(LIGHTS.setRegistryName(FeatureLib.LIGHTS));
//        }
//        private static ResourceKey<Biome> k(Biome b) {
//            return ResourceKey.create(Registry.BIOME_REGISTRY, Objects.requireNonNull(b.getRegistryName()));
//        }
//    }
}
