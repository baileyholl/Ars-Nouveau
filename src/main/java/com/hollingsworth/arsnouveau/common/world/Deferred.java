package com.hollingsworth.arsnouveau.common.world;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.tile.LightTile;
import com.hollingsworth.arsnouveau.common.world.feature.SingleBlockFeature;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.DiskFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedBlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

public class Deferred {
    public static final DeferredRegister<Feature<?>> FEAT_REG = DeferredRegister.create(ForgeRegistries.FEATURES, MODID);
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFG_REG = DeferredRegister.create(Registries.CONFIGURED_FEATURE, MODID);
    public static final DeferredRegister<PlacedFeature> PLACED_FEAT_REG = DeferredRegister.create(Registries.PLACED_FEATURE, MODID);


    public static final RegistryObject<Feature<BlockStateConfiguration>> LIGHT_FEATURE = FEAT_REG.register("lights", () -> new SingleBlockFeature(BlockStateConfiguration.CODEC) {
        @Override
        public boolean place(FeaturePlaceContext<BlockStateConfiguration> pContext) {
            return false;
        }

        @Override
        public void onStatePlace(WorldGenLevel seed, ChunkGenerator chunkGenerator, RandomSource rand, BlockPos pos, BlockStateConfiguration config) {
            if (seed instanceof WorldGenRegion world) {
                RandomSource random = world.getRandom();
                if (world.getBlockEntity(pos) instanceof LightTile tile) {
                    tile.color = new ParticleColor(
                            Math.max(10, random.nextInt(255)),
                            Math.max(10, random.nextInt(255)),
                            Math.max(10, random.nextInt(255))
                    );
                }
            }
        }
    });
    public static final RegistryObject<Feature<DiskConfiguration>> DISK = FEAT_REG.register("disk", () -> new DiskFeature(DiskConfiguration.CODEC));


    public static final RegistryObject<ConfiguredFeature<?, ?>> DISK_CLAY = CONFG_REG.register("disk_clay", () ->new ConfiguredFeature<>(DISK.get(), new DiskConfiguration(RuleBasedBlockStateProvider.simple(Blocks.CLAY), BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, Blocks.CLAY)), UniformInt.of(2, 3), 1)));
    public static final RegistryObject<ConfiguredFeature<?, ?>> DISK_SAND = CONFG_REG.register("disk_sand", () ->new ConfiguredFeature<>(DISK.get(), new DiskConfiguration(new RuleBasedBlockStateProvider(BlockStateProvider.simple(Blocks.SAND), List.of(new RuleBasedBlockStateProvider.Rule(BlockPredicate.matchesBlocks(Direction.DOWN.getNormal(), Blocks.AIR), BlockStateProvider.simple(Blocks.SANDSTONE)))), BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, Blocks.GRASS_BLOCK)), UniformInt.of(2, 6), 2)));
    public static final RegistryObject<ConfiguredFeature<?, ?>> DISK_GRAVEL = CONFG_REG.register("disk_gravel", () ->new ConfiguredFeature<>(DISK.get(), new DiskConfiguration(RuleBasedBlockStateProvider.simple(Blocks.GRAVEL), BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, Blocks.GRASS_BLOCK)), UniformInt.of(2, 5), 2)));


    public static final RegistryObject<ConfiguredFeature<?, ?>> CONFIGURED_LIGHTS = CONFG_REG.register("configured_lights",
            () -> new ConfiguredFeature<>(LIGHT_FEATURE.get(), new BlockStateConfiguration(BlockRegistry.LIGHT_BLOCK.defaultBlockState())));



    public static final RegistryObject<PlacedFeature> DISK_CLAY_PLACED = PLACED_FEAT_REG.register("placed_disk_clay", () -> new
            PlacedFeature(Holder.direct(DISK_CLAY.get()), List.of(InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BlockPredicateFilter.forPredicate(BlockPredicate.matchesFluids(Fluids.WATER)), BiomeFilter.biome())));
    public static final RegistryObject<PlacedFeature> DISK_SAND_PLACED = PLACED_FEAT_REG.register("placed_disk_sand", () -> new
            PlacedFeature(Holder.direct(DISK_SAND.get()), List.of(CountPlacement.of(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BlockPredicateFilter.forPredicate(BlockPredicate.matchesFluids(Fluids.WATER)), BiomeFilter.biome())));
    public static final RegistryObject<PlacedFeature> DISK_GRAVEL_PLACED = PLACED_FEAT_REG.register("placed_disk_gravel", () -> new
            PlacedFeature(Holder.direct(DISK_GRAVEL.get()), List.of(InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BlockPredicateFilter.forPredicate(BlockPredicate.matchesFluids(Fluids.WATER)), BiomeFilter.biome())));


    public static final RegistryObject<PlacedFeature> PLACED_LIGHTS = PLACED_FEAT_REG.register("placed_lights", () ->
            new PlacedFeature(Holder.direct(CONFIGURED_LIGHTS.get()), VegetationPlacements.worldSurfaceSquaredWithCount(1)));

}
