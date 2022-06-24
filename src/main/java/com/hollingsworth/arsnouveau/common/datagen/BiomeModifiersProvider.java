package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.JsonElement;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.world.WorldEvent;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

public class BiomeModifiersProvider {
    static void datagenModifiers(GatherDataEvent event) {
        final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.builtinCopy());

        Map<ResourceLocation, BiomeModifier> modifierMap = new HashMap<>();
        HolderSet.Named<Biome> SUMMON_TAG = new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).orElseThrow(), BiomeTagProvider.SUMMON_SPAWN_TAG);
        HolderSet.Named<Biome> WILDEN_TAG = new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).orElseThrow(), BiomeTags.IS_OVERWORLD);
        HolderSet.Named<Biome> OVERWORLD_TAG = new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).orElseThrow(), BiomeTags.IS_OVERWORLD);

        modifierMap.put(STARBUNCLE_SPAWN, ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(SUMMON_TAG,
                new MobSpawnSettings.SpawnerData(ModEntities.STARBUNCLE_TYPE.get(),
                        5, 1, 2)
        ));
        modifierMap.put(DRYGMY_SPAWN, ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(SUMMON_TAG,
                new MobSpawnSettings.SpawnerData(ModEntities.ENTITY_DRYGMY.get(),
                        3, 1, 2)
        ));
        modifierMap.put(WHIRLISPRIG_SPAWN, ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(SUMMON_TAG,
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
        modifierMap.put(WILDEN_GUARDIAN_SPAWN, ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(WILDEN_TAG,
                new MobSpawnSettings.SpawnerData(ModEntities.WILDEN_GUARDIAN.get(),
                        50, 1, 1)
        ));
        // TODO: Change to direct, but causes a crash for some reason OR: Make a PlacedFeature tag for the trees instead
        HolderSet<PlacedFeature> TREESET = new HolderSet.Named<>(ops.registry(Registry.PLACED_FEATURE_REGISTRY).orElseThrow(), TagKey.create(Registry.PLACED_FEATURE_REGISTRY, WorldEvent.PLACED_MIXED_ID));
        modifierMap.put(ARCHWOOD_MIX_RARE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(OVERWORLD_TAG, TREESET, GenerationStep.Decoration.VEGETAL_DECORATION));

        event.getGenerator().addProvider(event.includeServer(), JsonCodecProvider.forDatapackRegistry(event.getGenerator(), event.getExistingFileHelper(), MODID,
                ops, ForgeRegistries.Keys.BIOME_MODIFIERS, modifierMap));
    }

    static final ResourceLocation STARBUNCLE_SPAWN = prefix("starbuncle_spawn");
    static final ResourceLocation DRYGMY_SPAWN = prefix("drygmy_spawn");
    static final ResourceLocation WHIRLISPRIG_SPAWN = prefix("whirlisprig_spawn");
    static final ResourceLocation WILDEN_HUNTER_SPAWN = prefix("wilden_hunter_spawn");
    static final ResourceLocation WILDEN_STALKER_SPAWN = prefix("wilden_stalker_spawn");
    static final ResourceLocation WILDEN_GUARDIAN_SPAWN = prefix("wilden_guardian_spawn");

    static final ResourceLocation ARCHWOOD_MIX_COMMON = prefix("common_archwood_mix");

    static final ResourceLocation ARCHWOOD_MIX_RARE = prefix("rare_archwood_mix");

    @NotNull
    private static ResourceLocation prefix(String path) {
        return new ResourceLocation(MODID, path);
    }
}
