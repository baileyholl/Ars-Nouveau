package com.hollingsworth.arsnouveau.common.world;

import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;

public class FeatureGen {

    public static void setupOreGen() {

        List<Biome.Category> categories = Arrays.asList(Biome.Category.FOREST, Biome.Category.EXTREME_HILLS, Biome.Category.JUNGLE,
                Biome.Category.PLAINS, Biome.Category.SWAMP, Biome.Category.SAVANNA, Biome.Category.TAIGA);
        for (Biome biome : ForgeRegistries.BIOMES) {
            if(Config.SPAWN_ORE.get()) {
                biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(
                        new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, BlockRegistry.ARCANE_ORE.getDefaultState(), 4)).withPlacement(Placement.COUNT_RANGE.configure(
                        new CountRangeConfig(5, 0, 0, 64))));
            }
            if(categories.contains(biome.getCategory())) {
                biome.getSpawns(EntityClassification.CREATURE).add(new Biome.SpawnListEntry(ModEntities.ENTITY_CARBUNCLE_TYPE, 5, 1, 3));
                biome.getSpawns(EntityClassification.CREATURE).add(new Biome.SpawnListEntry(ModEntities.ENTITY_SYLPH_TYPE, 5, 1, 2));
            }
        }
    }
}
