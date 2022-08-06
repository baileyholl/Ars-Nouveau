package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.JsonElement;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

public class BiomeModifiersProvider {
    static void datagenModifiers(GatherDataEvent event) {
        final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.builtinCopy());

        Map<ResourceLocation, BiomeModifier> modifierMap = new HashMap<>();
        HolderSet.Named<Biome> WILDEN_TAG = new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).orElseThrow(), BiomeTags.IS_OVERWORLD);
        HolderSet.Named<Biome> OVERWORLD_TAG = new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).orElseThrow(), BiomeTags.IS_OVERWORLD);
        HolderSet.Named<Biome> NO_SPAWN_HOSTILE = new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).orElseThrow(), BiomeTagProvider.NO_MOB_SPAWN);
        HolderSet.Named<Biome> COLD = new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).orElseThrow(), Tags.Biomes.IS_COLD_OVERWORLD);
        HolderSet.Named<EntityType<?>> HOSTILE = new HolderSet.Named<>(ops.registry(Registry.ENTITY_TYPE_REGISTRY).orElseThrow(), EntityTags.HOSTILE_MOBS);

        modifierMap.put(STARBUNCLE_SPAWN, ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(OVERWORLD_TAG,
                new MobSpawnSettings.SpawnerData(ModEntities.STARBUNCLE_TYPE.get(),
                        5, 1, 2)
        ));
        modifierMap.put(DRYGMY_SPAWN, ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(OVERWORLD_TAG,
                new MobSpawnSettings.SpawnerData(ModEntities.ENTITY_DRYGMY.get(),
                        3, 1, 2)
        ));
        modifierMap.put(WHIRLISPRIG_SPAWN, ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(OVERWORLD_TAG,
                new MobSpawnSettings.SpawnerData(ModEntities.WHIRLISPRIG_TYPE.get(),
                        5, 1, 2)
        ));
        modifierMap.put(WILDEN_HUNTER_SPAWN, ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(WILDEN_TAG,
                new MobSpawnSettings.SpawnerData(ModEntities.WILDEN_HUNTER.get(),
                        50, 1, 1)
        ));
        modifierMap.put(WILDEN_STALKER_SPAWN, ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(WILDEN_TAG,
                new MobSpawnSettings.SpawnerData(ModEntities.WILDEN_STALKER.get(),
                        50, 3, 3)
        ));
        modifierMap.put(WILDEN_GUARDIAN_SPAWN, ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(COLD,
                new MobSpawnSettings.SpawnerData(ModEntities.WILDEN_GUARDIAN.get(),
                        50, 1, 1)
        ));
        modifierMap.put(NO_SPAWN, new ForgeBiomeModifiers.RemoveSpawnsBiomeModifier(NO_SPAWN_HOSTILE, HOSTILE));

        HolderSet<PlacedFeature> TREESET = new HolderSet.Named<>(ops.registry(Registry.PLACED_FEATURE_REGISTRY).orElseThrow(), PlacedFeatureTagProvider.ARCHWOOD_TREES);

        modifierMap.put(ARCHWOOD_MIX_RARE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(OVERWORLD_TAG, TREESET,
                GenerationStep.Decoration.VEGETAL_DECORATION));

        HolderSet<PlacedFeature> BERRY_SET = new HolderSet.Named<>(ops.registry(Registry.PLACED_FEATURE_REGISTRY).orElseThrow(), PlacedFeatureTagProvider.SOURCE_BERRIES);
        modifierMap.put(BERRY_COMMON, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(COLD, BERRY_SET,
                GenerationStep.Decoration.VEGETAL_DECORATION));


        event.getGenerator().addProvider(event.includeServer(), JsonCodecProvider.forDatapackRegistry(event.getGenerator(), event.getExistingFileHelper(), MODID,
                ops, ForgeRegistries.Keys.BIOME_MODIFIERS, modifierMap));
    }

    static final ResourceLocation STARBUNCLE_SPAWN = prefix("starbuncle_spawn");
    static final ResourceLocation DRYGMY_SPAWN = prefix("drygmy_spawn");
    static final ResourceLocation WHIRLISPRIG_SPAWN = prefix("whirlisprig_spawn");
    static final ResourceLocation WILDEN_HUNTER_SPAWN = prefix("wilden_hunter_spawn");
    static final ResourceLocation WILDEN_STALKER_SPAWN = prefix("wilden_stalker_spawn");
    static final ResourceLocation WILDEN_GUARDIAN_SPAWN = prefix("wilden_guardian_spawn");
    static final ResourceLocation NO_SPAWN = prefix("no_spawn");

    static final ResourceLocation ARCHWOOD_MIX_COMMON = prefix("common_archwood_mix");

    static final ResourceLocation ARCHWOOD_MIX_RARE = prefix("rare_archwood_mix");
    static final ResourceLocation BERRY_COMMON = prefix("common_source_berry");

    @NotNull
    private static ResourceLocation prefix(String path) {
        return new ResourceLocation(MODID, path);
    }
}
