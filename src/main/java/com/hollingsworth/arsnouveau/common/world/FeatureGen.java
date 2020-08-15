package com.hollingsworth.arsnouveau.common.world;

import com.hollingsworth.arsnouveau.common.block.BlockRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.CountRange;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

public class FeatureGen {

    public static void setupOreGen() {
        System.out.println("REGISTERING ORE GEN");
        for (Biome biome : ForgeRegistries.BIOMES) {
            System.out.println(biome);
            biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(
                    new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, BlockRegistry.ARCANE_ORE.getDefaultState(), 9)).withPlacement(Placement.COUNT_RANGE.configure(
                    new CountRangeConfig(20,0,0,64))));

        }
    }
}
