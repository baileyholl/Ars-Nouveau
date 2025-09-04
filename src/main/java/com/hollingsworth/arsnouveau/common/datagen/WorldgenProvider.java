package com.hollingsworth.arsnouveau.common.datagen;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.registry.BannerRegistry;
import com.hollingsworth.arsnouveau.setup.registry.BiomeRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import com.hollingsworth.arsnouveau.setup.registry.WorldgenRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class WorldgenProvider extends DatapackBuiltinEntriesProvider {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, WorldgenRegistry::bootstrapConfiguredFeatures)
            .add(Registries.PLACED_FEATURE, WorldgenRegistry::bootstrapPlacedFeatures)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, BiomeModifierRegistry::bootstrap)
            .add(Registries.BIOME, BiomeRegistry::bootstrap)
            .add(Registries.BANNER_PATTERN, BannerRegistry::bootstrapPatterns);

    public WorldgenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(ArsNouveau.MODID));
    }

    public static class BiomeModifierRegistry {
        public static final ResourceKey<BiomeModifier> STARBUNCLE_SPAWN = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, prefix("starbuncle_spawn"));
        public static final ResourceKey<BiomeModifier> GIFT_STARBUNCLE_SPAWN = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, prefix("gift_starbuncle_spawn"));
        public static final ResourceKey<BiomeModifier> DRYGMY_SPAWN = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, prefix("drygmy_spawn"));
        public static final ResourceKey<BiomeModifier> ALARKINOS_SPAWN = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, prefix("alakarkinos_spawn"));
        public static final ResourceKey<BiomeModifier> WHIRLISPRIG_SPAWN = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, prefix("whirlisprig_spawn"));
        public static final ResourceKey<BiomeModifier> ARCHWOOD_MIX_RARE = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, prefix("rare_archwood_mix"));
        public static final ResourceKey<BiomeModifier> BERRY_COMMON = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, prefix("common_source_berry"));

        public static void bootstrap(BootstrapContext<BiomeModifier> context) {
            HolderSet<Biome> OVERWORLD_TAG = context.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_OVERWORLD);
            HolderSet<Biome> BEACH_TAG = context.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_BEACH);
            HolderSet.Named<Biome> BERRY_BIOMES = context.lookup(Registries.BIOME).getOrThrow(BiomeTagProvider.BERRY_SPAWN);
            Holder.Reference<PlacedFeature> BERRY_SET = context.lookup(Registries.PLACED_FEATURE).get(WorldgenRegistry.PLACED_BERRY_BUSH).get();
            Holder.Reference<PlacedFeature> TREE_SET = context.lookup(Registries.PLACED_FEATURE).get(WorldgenRegistry.PLACED_MIX_ARCHWOODS).get();
            context.register(STARBUNCLE_SPAWN, BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(OVERWORLD_TAG, new MobSpawnSettings.SpawnerData(ModEntities.STARBUNCLE_TYPE.get(),
                    5, 1, 2)));

            context.register(GIFT_STARBUNCLE_SPAWN, BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(OVERWORLD_TAG, new MobSpawnSettings.SpawnerData(ModEntities.GIFT_STARBY.get(),
                    1, 1, 1)));
            context.register(DRYGMY_SPAWN, BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(OVERWORLD_TAG, new MobSpawnSettings.SpawnerData(ModEntities.ENTITY_DRYGMY.get(),
                    3, 1, 2)));
            context.register(WHIRLISPRIG_SPAWN, BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(OVERWORLD_TAG, new MobSpawnSettings.SpawnerData(ModEntities.WHIRLISPRIG_TYPE.get(),
                    5, 1, 2)));

            context.register(BERRY_COMMON, new BiomeModifiers.AddFeaturesBiomeModifier(BERRY_BIOMES, HolderSet.direct(BERRY_SET), GenerationStep.Decoration.VEGETAL_DECORATION));
            context.register(ARCHWOOD_MIX_RARE, new BiomeModifiers.AddFeaturesBiomeModifier(OVERWORLD_TAG, HolderSet.direct(TREE_SET), GenerationStep.Decoration.VEGETAL_DECORATION));
            context.register(ALARKINOS_SPAWN, BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(BEACH_TAG, new MobSpawnSettings.SpawnerData(ModEntities.ALAKARKINOS_TYPE.get(),
                    5, 1, 1)));
        }

        @NotNull
        private static ResourceLocation prefix(String path) {
            return ArsNouveau.prefix(path);
        }

    }
}
