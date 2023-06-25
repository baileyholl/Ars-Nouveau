package com.hollingsworth.arsnouveau.common.datagen;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.world.WorldgenRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class WorldgenProvider extends DatapackBuiltinEntriesProvider {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, WorldgenRegistry::bootstrapConfiguredFeatures)
            .add(Registries.PLACED_FEATURE, WorldgenRegistry::bootstrapPlacedFeatures)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, BiomeModifierRegistry::bootstrap);

    public WorldgenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(ArsNouveau.MODID));
    }

    public static class BiomeModifierRegistry {
        public static final ResourceKey<BiomeModifier> STARBUNCLE_SPAWN = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, prefix("starbuncle_spawn"));
        public static final ResourceKey<BiomeModifier> GIFT_STARBUNCLE_SPAWN = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, prefix("gift_starbuncle_spawn"));
        public static final ResourceKey<BiomeModifier> DRYGMY_SPAWN = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, prefix("drygmy_spawn"));
        public static final ResourceKey<BiomeModifier> WHIRLISPRIG_SPAWN = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, prefix("whirlisprig_spawn"));
        public static final ResourceKey<BiomeModifier> WILDEN_HUNTER_SPAWN = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, prefix("wilden_hunter_spawn"));
        public static final ResourceKey<BiomeModifier> WILDEN_STALKER_SPAWN = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, prefix("wilden_stalker_spawn"));
        public static final ResourceKey<BiomeModifier> WILDEN_GUARDIAN_SPAWN = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, prefix("wilden_guardian_spawn"));
        public static final ResourceKey<BiomeModifier> NO_SPAWN = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, prefix("no_spawn"));
        public static final ResourceKey<BiomeModifier> ARCHWOOD_MIX_COMMON = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, prefix("common_archwood_mix"));
        public static final ResourceKey<BiomeModifier> ARCHWOOD_MIX_RARE = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, prefix("rare_archwood_mix"));
        public static final ResourceKey<BiomeModifier> BERRY_COMMON = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, prefix("common_source_berry"));

        public static void bootstrap(BootstapContext<BiomeModifier> context) {
            HolderSet<Biome> OVERWORLD_TAG = context.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_OVERWORLD);
            HolderSet.Named<Biome> BERRY_BIOMES = context.lookup(Registries.BIOME).getOrThrow(BiomeTagProvider.BERRY_SPAWN);
            Holder.Reference<PlacedFeature> BERRY_SET = context.lookup(Registries.PLACED_FEATURE).get(WorldgenRegistry.PLACED_BERRY_BUSH).get();
            Holder.Reference<PlacedFeature> TREE_SET = context.lookup(Registries.PLACED_FEATURE).get(WorldgenRegistry.PLACED_MIX_ARCHWOODS).get();
            context.register(STARBUNCLE_SPAWN, ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(OVERWORLD_TAG, new MobSpawnSettings.SpawnerData(ModEntities.STARBUNCLE_TYPE.get(),
                    5, 1, 2)));

            context.register(GIFT_STARBUNCLE_SPAWN, ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(OVERWORLD_TAG, new MobSpawnSettings.SpawnerData(ModEntities.GIFT_STARBY.get(),
                    1, 1, 1)));
            context.register(DRYGMY_SPAWN, ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(OVERWORLD_TAG, new MobSpawnSettings.SpawnerData(ModEntities.ENTITY_DRYGMY.get(),
                    3, 1, 2)));
            context.register(WHIRLISPRIG_SPAWN, ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(OVERWORLD_TAG, new MobSpawnSettings.SpawnerData(ModEntities.WHIRLISPRIG_TYPE.get(),
                    5, 1, 2)));

            context.register(BERRY_COMMON, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(BERRY_BIOMES, HolderSet.direct(BERRY_SET), GenerationStep.Decoration.VEGETAL_DECORATION));
            context.register(ARCHWOOD_MIX_RARE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(OVERWORLD_TAG, HolderSet.direct(TREE_SET), GenerationStep.Decoration.VEGETAL_DECORATION));
        }

        @NotNull
        private static ResourceLocation prefix(String path) {
            return new ResourceLocation(ArsNouveau.MODID, path);
        }
//    static void datagenModifiers(GatherDataEvent event) {
//        final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.builtinCopy());
//
//        Map<ResourceLocation, BiomeModifier> modifierMap = new HashMap<>();
//        HolderSet.Named<Biome> WILDEN_TAG = new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).orElseThrow(), BiomeTags.IS_OVERWORLD);
//        HolderSet.Named<Biome> OVERWORLD_TAG = new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).orElseThrow(), BiomeTags.IS_OVERWORLD);
//        HolderSet.Named<Biome> NO_SPAWN_HOSTILE = new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).orElseThrow(), BiomeTagProvider.NO_MOB_SPAWN);
//        HolderSet.Named<Biome> COLD = new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).orElseThrow(), Tags.Biomes.IS_COLD_OVERWORLD);
//        HolderSet.Named<Biome> BERRY_BIOMES = new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).orElseThrow(), BiomeTagProvider.BERRY_SPAWN);
//        HolderSet.Named<EntityType<?>> HOSTILE = new HolderSet.Named<>(ops.registry(Registry.ENTITY_TYPE_REGISTRY).orElseThrow(), EntityTags.HOSTILE_MOBS);
//
//        HolderSet<PlacedFeature> TREESET = new HolderSet.Named<>(ops.registry(Registry.PLACED_FEATURE_REGISTRY).orElseThrow(), PlacedFeatureTagProvider.ARCHWOOD_TREES);
//
//        modifierMap.put(ARCHWOOD_MIX_RARE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(OVERWORLD_TAG, TREESET,
//                GenerationStep.Decoration.VEGETAL_DECORATION));
//
//        HolderSet<PlacedFeature> BERRY_SET = new HolderSet.Named<>(ops.registry(Registry.PLACED_FEATURE_REGISTRY).orElseThrow(), PlacedFeatureTagProvider.SOURCE_BERRIES);
//        modifierMap.put(BERRY_COMMON, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(BERRY_BIOMES, BERRY_SET,
//                GenerationStep.Decoration.VEGETAL_DECORATION));
//
//
//        event.getGenerator().addProvider(event.includeServer(), JsonCodecProvider.forDatapackRegistry(event.getGenerator(), event.getExistingFileHelper(), MODID,
//                ops, ForgeRegistries.Keys.BIOME_MODIFIERS, modifierMap));
//    }
//
//    static final ResourceLocation STARBUNCLE_SPAWN = prefix("starbuncle_spawn");
//    static final ResourceLocation GIFT_STARBUNCLE_SPAWN = prefix("gift_starbuncle_spawn");
//    static final ResourceLocation DRYGMY_SPAWN = prefix("drygmy_spawn");
//    static final ResourceLocation WHIRLISPRIG_SPAWN = prefix("whirlisprig_spawn");
//    static final ResourceLocation WILDEN_HUNTER_SPAWN = prefix("wilden_hunter_spawn");
//    static final ResourceLocation WILDEN_STALKER_SPAWN = prefix("wilden_stalker_spawn");
//    static final ResourceLocation WILDEN_GUARDIAN_SPAWN = prefix("wilden_guardian_spawn");
//    static final ResourceLocation NO_SPAWN = prefix("no_spawn");
//
//    static final ResourceLocation ARCHWOOD_MIX_COMMON = prefix("common_archwood_mix");
//
//    static final ResourceLocation ARCHWOOD_MIX_RARE = prefix("rare_archwood_mix");
//    static final ResourceLocation BERRY_COMMON = prefix("common_source_berry");
//
//    @NotNull
//    private static ResourceLocation prefix(String path) {
//        return new ResourceLocation(MODID, path);
//    }
    }
}
