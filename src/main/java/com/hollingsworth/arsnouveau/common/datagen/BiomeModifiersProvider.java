package com.hollingsworth.arsnouveau.common.datagen;


import net.minecraftforge.common.world.BiomeModifier;

public class BiomeModifiersProvider extends BiomeMo {
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
//        modifierMap.put(STARBUNCLE_SPAWN, ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(OVERWORLD_TAG,
//                new MobSpawnSettings.SpawnerData(ModEntities.STARBUNCLE_TYPE.get(),
//                        5, 1, 2)
//        ));
//        modifierMap.put(GIFT_STARBUNCLE_SPAWN, ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(OVERWORLD_TAG,
//                new MobSpawnSettings.SpawnerData(ModEntities.GIFT_STARBY.get(),
//                        1, 1, 1)
//        ));
//        modifierMap.put(DRYGMY_SPAWN, ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(OVERWORLD_TAG,
//                new MobSpawnSettings.SpawnerData(ModEntities.ENTITY_DRYGMY.get(),
//                        3, 1, 2)
//        ));
//        modifierMap.put(WHIRLISPRIG_SPAWN, ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(OVERWORLD_TAG,
//                new MobSpawnSettings.SpawnerData(ModEntities.WHIRLISPRIG_TYPE.get(),
//                        5, 1, 2)
//        ));
//        modifierMap.put(NO_SPAWN, new ForgeBiomeModifiers.RemoveSpawnsBiomeModifier(NO_SPAWN_HOSTILE, HOSTILE));
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
