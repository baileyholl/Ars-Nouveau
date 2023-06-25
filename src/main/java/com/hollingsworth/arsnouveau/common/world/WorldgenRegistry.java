package com.hollingsworth.arsnouveau.common.world;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.world.tree.MagicTrunkPlacer;
import com.hollingsworth.arsnouveau.common.world.tree.SupplierBlockStateProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

public class WorldgenRegistry {
    public static final DeferredRegister<Feature<?>> FEAT_REG = DeferredRegister.create(ForgeRegistries.FEATURES, MODID);
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFG_REG = DeferredRegister.create(Registries.CONFIGURED_FEATURE, MODID);
    public static final DeferredRegister<PlacedFeature> PLACED_FEAT_REG = DeferredRegister.create(Registries.PLACED_FEATURE, MODID);


    public static final ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_CASCADING_TREE = registerConfKey("cascading_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_BLAZING_TREE = registerConfKey("blazing_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_VEXING_TREE = registerConfKey("vexing_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_FLOURISHING_TREE = registerConfKey("flourishing_tree");

    public static ResourceKey<Feature<?>> registerFeatureKey(String name) {
        return ResourceKey.create(Registries.FEATURE, new ResourceLocation(MODID, name));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerConfKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(MODID, name));
    }

    public static void boostrapFeatures(BootstapContext<Feature<?>> context){

    }

    public static void bootstrapConfiguredFeatures(BootstapContext<ConfiguredFeature<?, ?>> context) {
        context.register(CONFIGURED_CASCADING_TREE,new ConfiguredFeature<>(Feature.TREE, buildTree(LibBlockNames.CASCADING_LEAVES, LibBlockNames.CASCADING_LOG, false, new ResourceLocation(ArsNouveau.MODID, LibBlockNames.FROSTAYA_POD))));
        context.register(CONFIGURED_BLAZING_TREE,new ConfiguredFeature<>(Feature.TREE, buildTree(LibBlockNames.BLAZING_LEAVES, LibBlockNames.BLAZING_LOG, false, new ResourceLocation(ArsNouveau.MODID, LibBlockNames.BOMBEGRANATE_POD))));
        context.register(CONFIGURED_VEXING_TREE,new ConfiguredFeature<>(Feature.TREE, buildTree(LibBlockNames.VEXING_LEAVES, LibBlockNames.VEXING_LOG, false, new ResourceLocation(ArsNouveau.MODID, LibBlockNames.BASTION_POD))));
        context.register(CONFIGURED_FLOURISHING_TREE,new ConfiguredFeature<>(Feature.TREE, buildTree(LibBlockNames.FLOURISHING_LEAVES, LibBlockNames.FLOURISHING_LOG, false, new ResourceLocation(ArsNouveau.MODID, LibBlockNames.MENDOSTEEN_POD))));
    }

    public static TreeConfiguration buildTree(String leaves, String log, boolean natural, ResourceLocation podRegistryName) {
        return new TreeConfiguration.TreeConfigurationBuilder(new SupplierBlockStateProvider(log),
                new MagicTrunkPlacer(9, 1, 0, natural, podRegistryName.toString()),
                new SupplierBlockStateProvider(leaves),
                new BlobFoliagePlacer(UniformInt.of(0, 0), UniformInt.of(0, 0), 0),
                new TwoLayersFeatureSize(2, 0, 2)).build();
    }
//
//
//    public static final RegistryObject<Feature<BlockStateConfiguration>> LIGHT_FEATURE = FEAT_REG.register("lights", () -> new SingleBlockFeature(BlockStateConfiguration.CODEC) {
//        @Override
//        public boolean place(FeaturePlaceContext<BlockStateConfiguration> pContext) {
//            return false;
//        }
//
//        @Override
//        public void onStatePlace(WorldGenLevel seed, ChunkGenerator chunkGenerator, RandomSource rand, BlockPos pos, BlockStateConfiguration config) {
//            if (seed instanceof WorldGenRegion world) {
//                RandomSource random = world.getRandom();
//                if (world.getBlockEntity(pos) instanceof LightTile tile) {
//                    tile.color = new ParticleColor(
//                            Math.max(10, random.nextInt(255)),
//                            Math.max(10, random.nextInt(255)),
//                            Math.max(10, random.nextInt(255))
//                    );
//                }
//            }
//        }
//    });
//    public static final RegistryObject<Feature<DiskConfiguration>> DISK = FEAT_REG.register("disk", () -> new DiskFeature(DiskConfiguration.CODEC));
//
//
//    public static final RegistryObject<ConfiguredFeature<?, ?>> DISK_CLAY = CONFG_REG.register("disk_clay", () ->new ConfiguredFeature<>(DISK.get(), new DiskConfiguration(RuleBasedBlockStateProvider.simple(Blocks.CLAY), BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, Blocks.CLAY)), UniformInt.of(2, 3), 1)));
//    public static final RegistryObject<ConfiguredFeature<?, ?>> DISK_SAND = CONFG_REG.register("disk_sand", () ->new ConfiguredFeature<>(DISK.get(), new DiskConfiguration(new RuleBasedBlockStateProvider(BlockStateProvider.simple(Blocks.SAND), List.of(new RuleBasedBlockStateProvider.Rule(BlockPredicate.matchesBlocks(Direction.DOWN.getNormal(), Blocks.AIR), BlockStateProvider.simple(Blocks.SANDSTONE)))), BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, Blocks.GRASS_BLOCK)), UniformInt.of(2, 6), 2)));
//    public static final RegistryObject<ConfiguredFeature<?, ?>> DISK_GRAVEL = CONFG_REG.register("disk_gravel", () ->new ConfiguredFeature<>(DISK.get(), new DiskConfiguration(RuleBasedBlockStateProvider.simple(Blocks.GRAVEL), BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, Blocks.GRASS_BLOCK)), UniformInt.of(2, 5), 2)));
//
//
//    public static final RegistryObject<ConfiguredFeature<?, ?>> CONFIGURED_LIGHTS = CONFG_REG.register("configured_lights",
//            () -> new ConfiguredFeature<>(LIGHT_FEATURE.get(), new BlockStateConfiguration(BlockRegistry.LIGHT_BLOCK.defaultBlockState())));
//
//
//
//    public static final RegistryObject<PlacedFeature> DISK_CLAY_PLACED = PLACED_FEAT_REG.register("placed_disk_clay", () -> new
//            PlacedFeature(Holder.direct(DISK_CLAY.get()), List.of(InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BlockPredicateFilter.forPredicate(BlockPredicate.matchesFluids(Fluids.WATER)), BiomeFilter.biome())));
//    public static final RegistryObject<PlacedFeature> DISK_SAND_PLACED = PLACED_FEAT_REG.register("placed_disk_sand", () -> new
//            PlacedFeature(Holder.direct(DISK_SAND.get()), List.of(CountPlacement.of(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BlockPredicateFilter.forPredicate(BlockPredicate.matchesFluids(Fluids.WATER)), BiomeFilter.biome())));
//    public static final RegistryObject<PlacedFeature> DISK_GRAVEL_PLACED = PLACED_FEAT_REG.register("placed_disk_gravel", () -> new
//            PlacedFeature(Holder.direct(DISK_GRAVEL.get()), List.of(InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BlockPredicateFilter.forPredicate(BlockPredicate.matchesFluids(Fluids.WATER)), BiomeFilter.biome())));
//
//
//    public static final RegistryObject<PlacedFeature> PLACED_LIGHTS = PLACED_FEAT_REG.register("placed_lights", () ->
//            new PlacedFeature(Holder.direct(CONFIGURED_LIGHTS.get()), VegetationPlacements.worldSurfaceSquaredWithCount(1)));

}
