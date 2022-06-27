package com.hollingsworth.arsnouveau.common.world;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.SourceBerryBush;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.world.tree.MagicTrunkPlacer;
import com.hollingsworth.arsnouveau.common.world.tree.SupplierBlockStateProvider;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;
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

    public static Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> PATCH_BERRY_BUSH = FeatureUtils.register("ars_nouveau:patch_berry", Feature.RANDOM_PATCH,
        FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ArsNouveau.MODID,LibBlockNames.SOURCEBERRY_BUSH))
                        .defaultBlockState().setValue(SourceBerryBush.AGE, 3))), List.of(Blocks.GRASS_BLOCK)));

    public static Holder<PlacedFeature> BERRY_BUSH_PATCH_CONFIG = PlacementUtils.register("ars_nouveau:placed_berry", PATCH_BERRY_BUSH, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
    public static Holder<PlacedFeature> PLACED_CASCADE = PlacementUtils.register("ars_nouveau:placed_cascade", CASCADING_TREE, PlacementUtils.filteredByBlockSurvival(BlockRegistry.CASCADING_SAPLING));
    public static Holder<PlacedFeature> PLACED_BLAZING = PlacementUtils.register("ars_nouveau:placed_blazing", BLAZING_TREE, PlacementUtils.filteredByBlockSurvival(BlockRegistry.BLAZING_SAPLING));
    public static Holder<PlacedFeature> PLACED_VEXING = PlacementUtils.register("ars_nouveau:placed_vexing", VEXING_TREE, PlacementUtils.filteredByBlockSurvival(BlockRegistry.VEXING_SAPLING));
    public static Holder<PlacedFeature> PLACED_FLOURISHING = PlacementUtils.register("ars_nouveau:placed_flourishing", FLOURISHING_TREE,
            PlacementUtils.filteredByBlockSurvival(BlockRegistry.FLOURISHING_SAPLING));

    public static Holder<PlacedFeature> COMMON_FLOURISHING = PlacementUtils.register("ars_nouveau:common_flourishing",
            FLOURISHING_TREE,
            List.of(PlacementUtils.countExtra(12, 0.01f, 1), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, PlacementUtils.filteredByBlockSurvival(BlockRegistry.FLOURISHING_SAPLING)));

    public static Holder<PlacedFeature> COMMON_CASCADING = PlacementUtils.register("ars_nouveau:common_cascading",
            CASCADING_TREE,
            List.of(PlacementUtils.countExtra(12, 0.01f, 1), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, PlacementUtils.filteredByBlockSurvival(BlockRegistry.CASCADING_SAPLING)));

    public static Holder<PlacedFeature> COMMON_BLAZING = PlacementUtils.register("ars_nouveau:common_blazing",
            BLAZING_TREE,
            List.of(PlacementUtils.countExtra(12, 0.01f, 1), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, PlacementUtils.filteredByBlockSurvival(BlockRegistry.BLAZING_SAPLING)));

    public static Holder<PlacedFeature> COMMON_VEXING = PlacementUtils.register("ars_nouveau:common_vexing",
            VEXING_TREE,
           List.of(PlacementUtils.countExtra(12, 0.01f, 1),PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,  PlacementUtils.filteredByBlockSurvival(BlockRegistry.VEXING_SAPLING)));

    public static Holder<ConfiguredFeature<SimpleRandomFeatureConfiguration, ?>> MIXED_TREES = FeatureUtils.register("ars_nouveau:random_mixed", Feature.SIMPLE_RANDOM_SELECTOR, new SimpleRandomFeatureConfiguration(
            HolderSet.direct(PLACED_CASCADE, PLACED_BLAZING, PLACED_VEXING, PLACED_FLOURISHING)));



    public static Holder<ConfiguredFeature<SimpleRandomFeatureConfiguration, ?>> MIXED_COMMON_TREES = FeatureUtils.register("ars_nouveau:random_common_archwood", Feature.SIMPLE_RANDOM_SELECTOR, new SimpleRandomFeatureConfiguration(
                   HolderSet.direct(COMMON_FLOURISHING, COMMON_BLAZING, COMMON_VEXING, COMMON_CASCADING)));

    public static ResourceLocation PLACED_MIXED_ID = new ResourceLocation(ArsNouveau.MODID, "archwood");

    public static Holder<PlacedFeature> PLACED_MIXED = PlacementUtils.register(PLACED_MIXED_ID.toString(), MIXED_TREES, VegetationPlacements.treePlacement(RarityFilter.onAverageOnceEvery(Config.TREE_SPAWN_RATE)));

    public static Holder<PlacedFeature> COMMON_ARCHWOOD = PlacementUtils.register("ars_nouveau:common_archwood",
            MIXED_COMMON_TREES,
            VegetationPlacements.treePlacement(CountPlacement.of(3), BlockRegistry.BLAZING_SAPLING));

}
