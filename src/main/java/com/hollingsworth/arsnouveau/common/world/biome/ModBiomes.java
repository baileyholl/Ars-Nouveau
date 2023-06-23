package com.hollingsworth.arsnouveau.common.world.biome;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.world.level.biome.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBiomes {
    public static final ResourceKey<Biome> ARCHWOOD_FOREST = register("archwood_forest");

    public static ResourceKey<Biome> register(String name) {
        return ResourceKey.create(Registries.BIOME, new ResourceLocation(ArsNouveau.MODID, name));
    }

    public static void registerBiomes(IForgeRegistry<Biome> registry) {
//        registry.register(ARCHWOOD_FOREST.location(), archwoodForest());
    }

    @Nullable
    static final Music NORMAL_MUSIC = null;

    @SuppressWarnings("SameParameterValue")
    public static Biome biome(Biome.Precipitation precipitation, float temperature, float downfall, int waterColor, int waterFogColor, int skyColor, int grassColor, int foliageColor, MobSpawnSettings.Builder spawnBuilder, BiomeGenerationSettings.Builder biomeBuilder, Supplier<Music> music) {
        return new Biome.BiomeBuilder()
                .temperature(temperature)
                .downfall(downfall)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .grassColorOverride(grassColor)
                        .foliageColorOverride(foliageColor)
                        .waterColor(waterColor)
                        .waterFogColor(waterFogColor)
                        .fogColor(12638463)
                        .skyColor(skyColor)
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                        .backgroundMusic(music.get()).build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(biomeBuilder.build())
                .build();
    }

    public static void globalOverworldGeneration(BiomeGenerationSettings.Builder builder) {
        BiomeDefaultFeatures.addDefaultCarversAndLakes(builder);
        BiomeDefaultFeatures.addDefaultCrystalFormations(builder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(builder);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(builder);
        BiomeDefaultFeatures.addDefaultSprings(builder);
        BiomeDefaultFeatures.addSurfaceFreezing(builder);

    }
//
//    public static Biome archwoodForest() {
//        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
//        spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.STARBUNCLE_TYPE.get(), 2, 3, 5));
//        spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.ENTITY_DRYGMY.get(), 2, 1, 3));
//        spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.WHIRLISPRIG_TYPE.get(), 2, 1, 3));
//
//        spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.ENTITY_BLAZING_WEALD.get(), 3, 1, 1));
//        spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.ENTITY_CASCADING_WEALD.get(), 3, 1, 1));
//        spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.ENTITY_FLOURISHING_WEALD.get(), 3, 1, 1));
//        spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.ENTITY_VEXING_WEALD.get(), 3, 1, 1));
//        spawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 5, 4, 4));
//
//        BiomeDefaultFeatures.farmAnimals(spawnBuilder);
//        BiomeDefaultFeatures.commonSpawns(spawnBuilder);
//
//        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder();
//        //we need to follow the same order as vanilla biomes for the BiomeDefaultFeatures
//        globalOverworldGeneration(biomeBuilder);
//        BiomeDefaultFeatures.addMossyStoneBlock(biomeBuilder);
//        BiomeDefaultFeatures.addForestFlowers(biomeBuilder);
//        BiomeDefaultFeatures.addFerns(biomeBuilder);
//        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
//        BiomeDefaultFeatures.addExtraGold(biomeBuilder);
//        DefaultFeatures.softDisks(biomeBuilder);
//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_PLAINS);
//
//        BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);
//        BiomeDefaultFeatures.addDefaultExtraVegetation(biomeBuilder);
//        // TODO: restore placed lights
////        biomeBuilder.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, BuiltinRegistries.PLACED_FEATURE.getHolderOrThrow(
////                ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation(ArsNouveau.MODID, "placed_lights"))));
//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, WorldEvent.COMMON_ARCHWOOD);
//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, WorldEvent.ArtisanalMojangGrassTM);
//        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, WorldEvent.ArtisanalMojangFlowersTM);
//        return biome(Biome.Precipitation.RAIN, 0.7F, 0.8F, 7978751, 329011, 7978751, 2031567, 2210437, spawnBuilder, biomeBuilder, () -> Musics.createGameMusic(SoundRegistry.ARIA_BIBLIO.get()));
//    }

}
