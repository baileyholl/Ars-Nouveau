package com.hollingsworth.arsnouveau.common.world;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.SourceBerryBush;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.world.tree.MagicTrunkPlacer;
import com.hollingsworth.arsnouveau.common.world.tree.SupplierBlockStateProvider;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class WorldEvent {
    public static Holder<ConfiguredFeature<TreeConfiguration, ?>> CASCADING_TREE =  FeatureUtils.register("ars_nouveau:cascade_feature", Feature.TREE, (
            new TreeConfiguration.TreeConfigurationBuilder(new SupplierBlockStateProvider(LibBlockNames.CASCADING_LOG),
                    new MagicTrunkPlacer(9, 1, 0),
                    new SupplierBlockStateProvider(LibBlockNames.CASCADING_LEAVES),
                    new BlobFoliagePlacer(UniformInt.of(0, 0), UniformInt.of(0,0), 0),
                    new TwoLayersFeatureSize(2, 0, 2))).ignoreVines().build());

    public static Holder<ConfiguredFeature<TreeConfiguration, ?>> BLAZING_TREE =  FeatureUtils.register("ars_nouveau:blazing_feature", Feature.TREE, (
            new TreeConfiguration.TreeConfigurationBuilder(new SupplierBlockStateProvider(LibBlockNames.BLAZING_LOG),
                    new MagicTrunkPlacer(9, 1, 0),
                    new SupplierBlockStateProvider(LibBlockNames.BLAZING_LEAVES),
                    new BlobFoliagePlacer(UniformInt.of(0, 0), UniformInt.of(0, 0), 0),
                    new TwoLayersFeatureSize(2, 0, 2))).ignoreVines().build());

    public static Holder<ConfiguredFeature<TreeConfiguration, ?>> FLOURISHING_TREE =  FeatureUtils.register("ars_nouveau:flourishing_feature", Feature.TREE, (
            new TreeConfiguration.TreeConfigurationBuilder(new SupplierBlockStateProvider(LibBlockNames.FLOURISHING_LOG),
                    new MagicTrunkPlacer(9, 1, 0),
                    new SupplierBlockStateProvider(LibBlockNames.FLOURISHING_LEAVES),
                    new BlobFoliagePlacer(UniformInt.of(0, 0), UniformInt.of(0, 0), 0),
                    new TwoLayersFeatureSize(2, 0, 2))).ignoreVines().build());

    public static Holder<ConfiguredFeature<TreeConfiguration, ?>> VEXING_TREE =  FeatureUtils.register("ars_nouveau:vexing_feature", Feature.TREE, (
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
    public static Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> PATCH_BERRY_BUSH = FeatureUtils.register("ars_nouveau:patch_berry", Feature.RANDOM_PATCH,
        FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ArsNouveau.MODID,LibBlockNames.SOURCEBERRY_BUSH))
                        .defaultBlockState().setValue(SourceBerryBush.AGE, 3))), List.of(Blocks.GRASS_BLOCK)));

    public static Holder<PlacedFeature> BERRY_BUSH_PATCH_CONFIG = PlacementUtils.register("ars_nouveau:placed_berry", PATCH_BERRY_BUSH, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
    public static Holder<PlacedFeature> PLACED_CASCADE = PlacementUtils.register("ars_nouveau:placed_cascade", CASCADING_TREE, PlacementUtils.filteredByBlockSurvival(BlockRegistry.CASCADING_SAPLING));
    public static Holder<PlacedFeature> PLACED_BLAZING = PlacementUtils.register("ars_nouveau:placed_blazing", BLAZING_TREE, PlacementUtils.filteredByBlockSurvival(BlockRegistry.BLAZING_SAPLING));
    public static Holder<PlacedFeature> PLACED_VEXING = PlacementUtils.register("ars_nouveau:placed_vexing", VEXING_TREE, PlacementUtils.filteredByBlockSurvival(BlockRegistry.VEXING_SAPLING));
    public static Holder<PlacedFeature> PLACED_FLOURISHING = PlacementUtils.register("ars_nouveau:placed_flourishing", FLOURISHING_TREE, PlacementUtils.filteredByBlockSurvival(BlockRegistry.FLOURISHING_SAPLING));

    public static Holder<ConfiguredFeature<RandomFeatureConfiguration, ?>> MIXED_TREES = FeatureUtils.register("ars_nouveau:random_cascade", Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of(
            new WeightedPlacedFeature(PLACED_CASCADE, 0.25f),
            new WeightedPlacedFeature(PLACED_BLAZING, 0.25f),
            new WeightedPlacedFeature(PLACED_VEXING, 0.25f),
            new WeightedPlacedFeature(PLACED_FLOURISHING, 0.25f)), PLACED_CASCADE));

    public static Holder<PlacedFeature> PLACED_MIXED = PlacementUtils.register("ars_nouveau:archwood", MIXED_TREES, VegetationPlacements.treePlacement(RarityFilter.onAverageOnceEvery(Config.TREE_SPAWN_RATE)));
    public static Holder<PlacedFeature> COMMON_ARCHWOOD = PlacementUtils.register("ars_nouveau:common_archwood", MIXED_TREES, VegetationPlacements.treePlacement(RarityFilter.onAverageOnceEvery(1)));

    public static void registerFeatures() {

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
//    }
//
//    public static Biome blazingForest = VanillaBiomes.theVoidBiome().setRegistryName(ArsNouveau.MODID, "blazing_archwood_forest");


}
