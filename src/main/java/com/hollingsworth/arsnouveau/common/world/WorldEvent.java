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
import net.minecraft.data.worldgen.features.VegetationFeatures;
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
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

import static net.minecraft.data.worldgen.placement.VegetationPlacements.worldSurfaceSquaredWithCount;

public class WorldEvent {


    public static Holder<ConfiguredFeature<TreeConfiguration, ?>> CASCADING_TREE = FeatureUtils.register("ars_nouveau:cascade_feature",
            Feature.TREE,
            buildTree(LibBlockNames.CASCADING_LEAVES, LibBlockNames.CASCADING_LOG, false, new ResourceLocation(ArsNouveau.MODID, LibBlockNames.FROSTAYA_POD)));

    public static Holder<ConfiguredFeature<TreeConfiguration, ?>> BLAZING_TREE = FeatureUtils.register("ars_nouveau:blazing_feature",
            Feature.TREE, (
                    buildTree(LibBlockNames.BLAZING_LEAVES, LibBlockNames.BLAZING_LOG, false, new ResourceLocation(ArsNouveau.MODID, LibBlockNames.BOMBEGRANATE_POD))));

    public static Holder<ConfiguredFeature<TreeConfiguration, ?>> FLOURISHING_TREE = FeatureUtils.register("ars_nouveau:flourishing_feature",
            Feature.TREE,
            buildTree(LibBlockNames.FLOURISHING_LEAVES, LibBlockNames.FLOURISHING_LOG, false, new ResourceLocation(ArsNouveau.MODID, LibBlockNames.MENDOSTEEN_POD)));

    public static Holder<ConfiguredFeature<TreeConfiguration, ?>> VEXING_TREE = FeatureUtils.register("ars_nouveau:vexing_feature",
            Feature.TREE, (
                    buildTree(LibBlockNames.VEXING_LEAVES, LibBlockNames.VEXING_LOG, false, new ResourceLocation(ArsNouveau.MODID, LibBlockNames.BASTION_POD))));

    public static Holder<ConfiguredFeature<TreeConfiguration, ?>> NATURAL_CASCADE_TREE = FeatureUtils.register("ars_nouveau:natural_cascade_feature",
            Feature.TREE,
            buildTree(LibBlockNames.CASCADING_LEAVES, LibBlockNames.CASCADING_LOG, true,new ResourceLocation(ArsNouveau.MODID, LibBlockNames.FROSTAYA_POD) ));

    public static Holder<ConfiguredFeature<TreeConfiguration, ?>> NATURAL_BLAZING_TREE = FeatureUtils.register("ars_nouveau:natural_blazing_feature",
            Feature.TREE,
            buildTree(LibBlockNames.BLAZING_LEAVES, LibBlockNames.BLAZING_LOG, true, new ResourceLocation(ArsNouveau.MODID, LibBlockNames.BOMBEGRANATE_POD)));

    public static Holder<ConfiguredFeature<TreeConfiguration, ?>> NATURAL_FLOURISHING_TREE = FeatureUtils.register("ars_nouveau:natural_flourishing_feature",
            Feature.TREE,
            buildTree(LibBlockNames.FLOURISHING_LEAVES, LibBlockNames.FLOURISHING_LOG, true, new ResourceLocation(ArsNouveau.MODID, LibBlockNames.MENDOSTEEN_POD)));

    public static Holder<ConfiguredFeature<TreeConfiguration, ?>> NATURAL_VEXING_TREE = FeatureUtils.register("ars_nouveau:natural_vexing_feature",
            Feature.TREE,
            buildTree(LibBlockNames.VEXING_LEAVES, LibBlockNames.VEXING_LOG, true, new ResourceLocation(ArsNouveau.MODID, LibBlockNames.BASTION_POD)));

    public static TreeConfiguration buildTree(String leaves, String log, boolean natural, ResourceLocation podRegistryName) {
        return new TreeConfiguration.TreeConfigurationBuilder(new SupplierBlockStateProvider(log),
                new MagicTrunkPlacer(9, 1, 0, natural, podRegistryName.toString()),
                new SupplierBlockStateProvider(leaves),
                new BlobFoliagePlacer(UniformInt.of(0, 0), UniformInt.of(0, 0), 0),
                new TwoLayersFeatureSize(2, 0, 2)).build();
    }

    public static Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> PATCH_BERRY_BUSH = FeatureUtils.register("ars_nouveau:patch_berry", Feature.RANDOM_PATCH,
            FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                    new SimpleBlockConfiguration(BlockStateProvider.simple(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ArsNouveau.MODID, LibBlockNames.SOURCEBERRY_BUSH))
                            .defaultBlockState().setValue(SourceBerryBush.AGE, 3))), List.of(Blocks.GRASS_BLOCK)));

    public static Holder<PlacedFeature> BERRY_BUSH_PATCH_CONFIG = PlacementUtils.register("ars_nouveau:placed_berry", PATCH_BERRY_BUSH, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
    public static Holder<PlacedFeature> PLACED_CASCADE = PlacementUtils.register("ars_nouveau:placed_cascade", NATURAL_CASCADE_TREE, PlacementUtils.filteredByBlockSurvival(BlockRegistry.CASCADING_SAPLING));
    public static Holder<PlacedFeature> PLACED_BLAZING = PlacementUtils.register("ars_nouveau:placed_blazing", NATURAL_BLAZING_TREE, PlacementUtils.filteredByBlockSurvival(BlockRegistry.BLAZING_SAPLING));
    public static Holder<PlacedFeature> PLACED_VEXING = PlacementUtils.register("ars_nouveau:placed_vexing", NATURAL_VEXING_TREE, PlacementUtils.filteredByBlockSurvival(BlockRegistry.VEXING_SAPLING));
    public static Holder<PlacedFeature> PLACED_FLOURISHING = PlacementUtils.register("ars_nouveau:placed_flourishing", NATURAL_FLOURISHING_TREE,
            PlacementUtils.filteredByBlockSurvival(BlockRegistry.FLOURISHING_SAPLING));

    public static Holder<PlacedFeature> COMMON_FLOURISHING = PlacementUtils.register("ars_nouveau:common_flourishing",
            NATURAL_FLOURISHING_TREE,
            List.of(PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, PlacementUtils.filteredByBlockSurvival(BlockRegistry.FLOURISHING_SAPLING)));

    public static Holder<PlacedFeature> COMMON_CASCADING = PlacementUtils.register("ars_nouveau:common_cascading",
            NATURAL_CASCADE_TREE,
            List.of(PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, PlacementUtils.filteredByBlockSurvival(BlockRegistry.CASCADING_SAPLING)));

    public static Holder<PlacedFeature> COMMON_BLAZING = PlacementUtils.register("ars_nouveau:common_blazing",
            NATURAL_BLAZING_TREE,
            List.of(PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, PlacementUtils.filteredByBlockSurvival(BlockRegistry.BLAZING_SAPLING)));

    public static Holder<PlacedFeature> COMMON_VEXING = PlacementUtils.register("ars_nouveau:common_vexing",
            NATURAL_VEXING_TREE,
            List.of(PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, PlacementUtils.filteredByBlockSurvival(BlockRegistry.VEXING_SAPLING)));

    public static Holder<ConfiguredFeature<SimpleRandomFeatureConfiguration, ?>> MIXED_TREES = FeatureUtils.register("ars_nouveau:random_mixed", Feature.SIMPLE_RANDOM_SELECTOR, new SimpleRandomFeatureConfiguration(
            HolderSet.direct(PLACED_CASCADE, PLACED_BLAZING, PLACED_VEXING, PLACED_FLOURISHING)));


    public static Holder<ConfiguredFeature<SimpleRandomFeatureConfiguration, ?>> MIXED_COMMON_TREES = FeatureUtils.register("ars_nouveau:random_common_archwood", Feature.SIMPLE_RANDOM_SELECTOR, new SimpleRandomFeatureConfiguration(
            HolderSet.direct(COMMON_FLOURISHING, COMMON_BLAZING, COMMON_VEXING, COMMON_CASCADING)));

    public static ResourceLocation PLACED_MIXED_ID = new ResourceLocation(ArsNouveau.MODID, "archwood");

    public static Holder<PlacedFeature> PLACED_MIXED = PlacementUtils.register(PLACED_MIXED_ID.toString(),
            MIXED_TREES,
            VegetationPlacements.treePlacement(RarityFilter.onAverageOnceEvery(Config.TREE_SPAWN_RATE)));

    public static Holder<PlacedFeature> COMMON_ARCHWOOD = PlacementUtils.register("ars_nouveau:common_archwood",
            MIXED_COMMON_TREES,
            VegetationPlacements.treePlacement(PlacementUtils.countExtra(7, 0.01f, 1), BlockRegistry.BLAZING_SAPLING));

    // FEATURE CYCLE MY REEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
    public static Holder<PlacedFeature> ArtisanalMojangGrassTM = PlacementUtils.register("ars_nouveau:patch_grass_forest",
            VegetationFeatures.PATCH_GRASS,
            worldSurfaceSquaredWithCount(2));
    public static final Holder<PlacedFeature> ArtisanalMojangFlowersTM = PlacementUtils.register("ars_nouveau:flower_default",
            VegetationFeatures.FLOWER_DEFAULT, RarityFilter.onAverageOnceEvery(32),
            InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());


}
