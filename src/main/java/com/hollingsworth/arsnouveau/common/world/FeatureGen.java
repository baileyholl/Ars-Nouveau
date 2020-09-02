package com.hollingsworth.arsnouveau.common.world;

import com.hollingsworth.arsnouveau.common.block.BlockRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.ForgeRegistries;

public class FeatureGen {

    public static void setupOreGen() {
        for (Biome biome : ForgeRegistries.BIOMES) {
            biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(
                    new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, BlockRegistry.ARCANE_ORE.getDefaultState(), 9)).withPlacement(Placement.COUNT_RANGE.configure(
                    new CountRangeConfig(20,0,0,64))));

        }
    }
}
