package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.SourceBerryBush;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.world.feature.LightFeature;
import com.hollingsworth.arsnouveau.common.world.tree.MagicTrunkPlacer;
import com.hollingsworth.arsnouveau.common.world.tree.SupplierBlockStateProvider;
import com.hollingsworth.arsnouveau.setup.config.Config;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
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
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;
import static net.minecraft.data.worldgen.placement.VegetationPlacements.worldSurfaceSquaredWithCount;

public class WorldgenRegistry {
    public static final DeferredRegister<Feature<?>> FEAT_REG = DeferredRegister.create(BuiltInRegistries.FEATURE, MODID);
    public static final DeferredHolder<Feature<?>, LightFeature> LIGHT_FEATURE = FEAT_REG.register("light_feature", () -> new LightFeature(BlockStateConfiguration.CODEC));


    public static final ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_CASCADING_TREE = registerConfKey("cascading_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_BLAZING_TREE = registerConfKey("blazing_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_VEXING_TREE = registerConfKey("vexing_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_FLOURISHING_TREE = registerConfKey("flourishing_tree");

    public static final ResourceKey<ConfiguredFeature<?, ?>> NATURAL_CONFIGURED_CASCADING_TREE = registerConfKey("natural_cascading_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NATURAL_CONFIGURED_BLAZING_TREE = registerConfKey("natural_blazing_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NATURAL_CONFIGURED_VEXING_TREE = registerConfKey("natural_vexing_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NATURAL_CONFIGURED_FLOURISHING_TREE = registerConfKey("natural_flourishing_tree");


    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_BERRY_BUSH = registerConfKey("patch_berry");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_LIGHTS = registerConfKey("lights");

    public static final ResourceKey<PlacedFeature> PLACED_NATURAL_CASCADING_TREE = registerPlacedKey("placed_cascading_tree");
    public static final ResourceKey<PlacedFeature> PLACED_NATURAL_BLAZING_TREE = registerPlacedKey("placed_blazing_tree");
    public static final ResourceKey<PlacedFeature> PLACED_NATURAL_VEXING_TREE = registerPlacedKey("placed_vexing_tree");
    public static final ResourceKey<PlacedFeature> PLACED_NATURAL_FLOURISHING_TREE = registerPlacedKey("placed_flourishing_tree");

    public static final ResourceKey<PlacedFeature> PLACED_BERRY_BUSH = registerPlacedKey("placed_berry_bush");

    public static final ResourceKey<PlacedFeature> PLACED_MIX_ARCHWOODS = registerPlacedKey("placed_mixed_archwoods");
    public static final ResourceKey<PlacedFeature> PLACED_DENSE_ARCHWOODS = registerPlacedKey("placed_dense_archwoods");

    public static final ResourceKey<ConfiguredFeature<?, ?>> MIXED_ARCHWOODS = registerConfKey("mixed_archwoods");

    public static final ResourceKey<PlacedFeature> PLACED_MOJANK_GRASS = registerPlacedKey("mojang_grass");
    public static final ResourceKey<PlacedFeature> PLACED_MOJANK_FLOWERS = registerPlacedKey("mojang_flowers");
    public static final ResourceKey<PlacedFeature> PLACED_LIGHTS = registerPlacedKey("placed_lights");

    public static ResourceKey<Feature<?>> registerFeatureKey(String name) {
        return ResourceKey.create(Registries.FEATURE, ArsNouveau.prefix(name));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerConfKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ArsNouveau.prefix(name));
    }

    public static ResourceKey<PlacedFeature> registerPlacedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ArsNouveau.prefix(name));
    }

    public static void bootstrapConfiguredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<PlacedFeature> placed = context.lookup(Registries.PLACED_FEATURE);
        context.register(CONFIGURED_CASCADING_TREE, new ConfiguredFeature<>(Feature.TREE, buildTree(LibBlockNames.CASCADING_LEAVES, LibBlockNames.CASCADING_LOG, false, ArsNouveau.prefix(LibBlockNames.FROSTAYA_POD))));
        context.register(CONFIGURED_BLAZING_TREE, new ConfiguredFeature<>(Feature.TREE, buildTree(LibBlockNames.BLAZING_LEAVES, LibBlockNames.BLAZING_LOG, false, ArsNouveau.prefix(LibBlockNames.BOMBEGRANATE_POD))));
        context.register(CONFIGURED_VEXING_TREE, new ConfiguredFeature<>(Feature.TREE, buildTree(LibBlockNames.VEXING_LEAVES, LibBlockNames.VEXING_LOG, false, ArsNouveau.prefix(LibBlockNames.BASTION_POD))));
        context.register(CONFIGURED_FLOURISHING_TREE, new ConfiguredFeature<>(Feature.TREE, buildTree(LibBlockNames.FLOURISHING_LEAVES, LibBlockNames.FLOURISHING_LOG, false, ArsNouveau.prefix(LibBlockNames.MENDOSTEEN_POD))));

        context.register(NATURAL_CONFIGURED_CASCADING_TREE, new ConfiguredFeature<>(Feature.TREE, buildTree(LibBlockNames.CASCADING_LEAVES, LibBlockNames.CASCADING_LOG, true, ArsNouveau.prefix(LibBlockNames.FROSTAYA_POD))));
        context.register(NATURAL_CONFIGURED_BLAZING_TREE, new ConfiguredFeature<>(Feature.TREE, buildTree(LibBlockNames.BLAZING_LEAVES, LibBlockNames.BLAZING_LOG, true, ArsNouveau.prefix(LibBlockNames.BOMBEGRANATE_POD))));
        context.register(NATURAL_CONFIGURED_VEXING_TREE, new ConfiguredFeature<>(Feature.TREE, buildTree(LibBlockNames.VEXING_LEAVES, LibBlockNames.VEXING_LOG, true, ArsNouveau.prefix(LibBlockNames.BASTION_POD))));
        context.register(NATURAL_CONFIGURED_FLOURISHING_TREE, new ConfiguredFeature<>(Feature.TREE, buildTree(LibBlockNames.FLOURISHING_LEAVES, LibBlockNames.FLOURISHING_LOG, true, ArsNouveau.prefix(LibBlockNames.MENDOSTEEN_POD))));
        context.register(PATCH_BERRY_BUSH, new ConfiguredFeature<>(Feature.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(BuiltInRegistries.BLOCK.get(ArsNouveau.prefix(LibBlockNames.SOURCEBERRY_BUSH)).defaultBlockState().setValue(SourceBerryBush.AGE, 3))), List.of(Blocks.GRASS_BLOCK))));
        context.register(MIXED_ARCHWOODS, new ConfiguredFeature<>(Feature.SIMPLE_RANDOM_SELECTOR, new SimpleRandomFeatureConfiguration(HolderSet.direct(placed.getOrThrow(PLACED_NATURAL_CASCADING_TREE), placed.getOrThrow(PLACED_NATURAL_BLAZING_TREE), placed.getOrThrow(PLACED_NATURAL_VEXING_TREE), placed.getOrThrow(PLACED_NATURAL_FLOURISHING_TREE)))));
        context.register(CONFIGURED_LIGHTS, new ConfiguredFeature<>(WorldgenRegistry.LIGHT_FEATURE.get(), new BlockStateConfiguration(BlockRegistry.LIGHT_BLOCK.get().defaultBlockState())));
    }

    public static void bootstrapPlacedFeatures(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> features = context.lookup(Registries.CONFIGURED_FEATURE);
        context.register(PLACED_BERRY_BUSH, new PlacedFeature(features.get(PATCH_BERRY_BUSH).get(), List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome())));
        context.register(PLACED_NATURAL_CASCADING_TREE, new PlacedFeature(features.get(NATURAL_CONFIGURED_CASCADING_TREE).get(), List.of(PlacementUtils.filteredByBlockSurvival(BlockRegistry.CASCADING_SAPLING.get()))));
        context.register(PLACED_NATURAL_BLAZING_TREE, new PlacedFeature(features.get(NATURAL_CONFIGURED_BLAZING_TREE).get(), List.of(PlacementUtils.filteredByBlockSurvival(BlockRegistry.BLAZING_SAPLING.get()))));
        context.register(PLACED_NATURAL_VEXING_TREE, new PlacedFeature(features.get(NATURAL_CONFIGURED_VEXING_TREE).get(), List.of(PlacementUtils.filteredByBlockSurvival(BlockRegistry.VEXING_SAPLING.get()))));
        context.register(PLACED_NATURAL_FLOURISHING_TREE, new PlacedFeature(features.get(NATURAL_CONFIGURED_FLOURISHING_TREE).get(), List.of(PlacementUtils.filteredByBlockSurvival(BlockRegistry.FLOURISHING_SAPLING.get()))));
        context.register(PLACED_MIX_ARCHWOODS, new PlacedFeature(features.get(MIXED_ARCHWOODS).get(), VegetationPlacements.treePlacement(RarityFilter.onAverageOnceEvery(Config.TREE_SPAWN_RATE))));
        context.register(PLACED_MOJANK_GRASS, new PlacedFeature(features.get(VegetationFeatures.PATCH_GRASS).get(), worldSurfaceSquaredWithCount(2)));
        context.register(PLACED_MOJANK_FLOWERS, new PlacedFeature(features.get(VegetationFeatures.FLOWER_DEFAULT).get(), List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome())));
        context.register(PLACED_LIGHTS, new PlacedFeature(features.get(CONFIGURED_LIGHTS).get(), VegetationPlacements.worldSurfaceSquaredWithCount(1)));
        context.register(PLACED_DENSE_ARCHWOODS, new PlacedFeature(features.get(MIXED_ARCHWOODS).get(), VegetationPlacements.treePlacement(PlacementUtils.countExtra(7, 0.01f, 1))));
    }

    public static TreeConfiguration buildTree(String leaves, String log, boolean natural, ResourceLocation podRegistryName) {
        return new TreeConfiguration.TreeConfigurationBuilder(new SupplierBlockStateProvider(log),
                new MagicTrunkPlacer(9, 1, 0, natural, podRegistryName.toString()),
                new SupplierBlockStateProvider(leaves),
                new BlobFoliagePlacer(UniformInt.of(0, 0), UniformInt.of(0, 0), 0),
                new TwoLayersFeatureSize(2, 0, 2)).build();
    }
}
