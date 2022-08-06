package com.hollingsworth.arsnouveau.common.world;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;

public class DefaultFeatures {

    public static void softDisks(BiomeGenerationSettings.Builder pBuilder) {
        pBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, BuiltinRegistries.PLACED_FEATURE.getHolderOrThrow(
                ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation(ArsNouveau.MODID, "placed_disk_sand"))));
        pBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, BuiltinRegistries.PLACED_FEATURE.getHolderOrThrow(
                ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation(ArsNouveau.MODID, "placed_disk_clay"))));
        pBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, BuiltinRegistries.PLACED_FEATURE.getHolderOrThrow(
                ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation(ArsNouveau.MODID, "placed_disk_gravel"))));


    }


}
