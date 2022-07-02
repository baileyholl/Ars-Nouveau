package com.hollingsworth.arsnouveau.common.world;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.tile.LightTile;
import com.hollingsworth.arsnouveau.common.world.feature.SingleBlockFeature;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

public class Deferred {
    public static final DeferredRegister<Feature<?>> FEAT_REG = DeferredRegister.create(ForgeRegistries.FEATURES, MODID);
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFG_REG = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, MODID);
    public static final DeferredRegister<PlacedFeature> PLACED_FEAT_REG = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, MODID);
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
    public static final RegistryObject<ConfiguredFeature<?, ?>> CONFIGURED_LIGHTS = CONFG_REG.register("configured_lights",
            () -> new ConfiguredFeature<>(LIGHT_FEATURE.get(), new BlockStateConfiguration(BlockRegistry.LIGHT_BLOCK.defaultBlockState())));

    public static final RegistryObject<PlacedFeature> PLACED_LIGHTS = PLACED_FEAT_REG.register("placed_lights", () ->
            new PlacedFeature(Holder.direct(CONFIGURED_LIGHTS.get()), VegetationPlacements.worldSurfaceSquaredWithCount(1)));
}
