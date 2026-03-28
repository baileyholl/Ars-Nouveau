package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Musics;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;


public class BiomeRegistry {
    public static final ResourceKey<Biome> ARCHWOOD_FOREST = register("archwood_forest");

    public static ResourceKey<Biome> register(String name) {
        return ResourceKey.create(Registries.BIOME, ArsNouveau.prefix(name));
    }

    public static void bootstrap(BootstrapContext<Biome> context) {
        context.register(ARCHWOOD_FOREST, archwoodForest(context));
    }

    public static void globalOverworldGeneration(BiomeGenerationSettings.Builder builder) {
        BiomeDefaultFeatures.addDefaultCarversAndLakes(builder);
        BiomeDefaultFeatures.addDefaultCrystalFormations(builder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(builder);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(builder);
        BiomeDefaultFeatures.addDefaultSprings(builder);
        BiomeDefaultFeatures.addSurfaceFreezing(builder);

    }

    public static void softDisks(BiomeGenerationSettings.Builder pBuilder) {
        pBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MiscOverworldPlacements.DISK_SAND);
        pBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MiscOverworldPlacements.DISK_CLAY);
        pBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MiscOverworldPlacements.DISK_GRAVEL);
    }

    public static Biome archwoodForest(BootstrapContext<Biome> context) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        spawnBuilder.addSpawn(MobCategory.CREATURE, 2, new MobSpawnSettings.SpawnerData(ModEntities.STARBUNCLE_TYPE.get(), 3, 5));
        spawnBuilder.addSpawn(MobCategory.CREATURE, 2, new MobSpawnSettings.SpawnerData(ModEntities.ENTITY_DRYGMY.get(), 1, 3));
        spawnBuilder.addSpawn(MobCategory.CREATURE, 2, new MobSpawnSettings.SpawnerData(ModEntities.WHIRLISPRIG_TYPE.get(), 1, 3));

        spawnBuilder.addSpawn(MobCategory.CREATURE, 3, new MobSpawnSettings.SpawnerData(ModEntities.ENTITY_BLAZING_WEALD.get(), 1, 1));
        spawnBuilder.addSpawn(MobCategory.CREATURE, 3, new MobSpawnSettings.SpawnerData(ModEntities.ENTITY_CASCADING_WEALD.get(), 1, 1));
        spawnBuilder.addSpawn(MobCategory.CREATURE, 3, new MobSpawnSettings.SpawnerData(ModEntities.ENTITY_FLOURISHING_WEALD.get(), 1, 1));
        spawnBuilder.addSpawn(MobCategory.CREATURE, 3, new MobSpawnSettings.SpawnerData(ModEntities.ENTITY_VEXING_WEALD.get(), 1, 1));
        spawnBuilder.addSpawn(MobCategory.CREATURE, 5, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 4, 4));

        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
        //we need to follow the same order as vanilla biomes for the BiomeDefaultFeatures
        globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addMossyStoneBlock(biomeBuilder);
        BiomeDefaultFeatures.addForestFlowers(biomeBuilder);
        BiomeDefaultFeatures.addFerns(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
        BiomeDefaultFeatures.addExtraGold(biomeBuilder);
        softDisks(biomeBuilder);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_PLAINS);

        BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);
        BiomeDefaultFeatures.addDefaultExtraVegetation(biomeBuilder, false);
        biomeBuilder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, WorldgenRegistry.PLACED_LIGHTS);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, WorldgenRegistry.PLACED_DENSE_ARCHWOODS);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, WorldgenRegistry.PLACED_MOJANK_GRASS);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, WorldgenRegistry.PLACED_MOJANK_FLOWERS);
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(0.8f)
                .temperature(0.7f)
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(7978751)
                        .grassColorOverride(2031567)
                        .foliageColorOverride(2210437)
                        .build())
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, 329011)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, 7978751)
                .setAttribute(EnvironmentAttributes.FOG_COLOR, 12638463)
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
                .setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(Musics.createGameMusic(SoundRegistry.ARIA_BIBLIO)))
                .build();
    }

}
