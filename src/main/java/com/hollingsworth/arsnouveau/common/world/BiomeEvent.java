package com.hollingsworth.arsnouveau.common.world;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class BiomeEvent {
    @SubscribeEvent
    public static void biomeLoad(BiomeLoadingEvent e) {

        if (e.getCategory() == Biome.BiomeCategory.NETHER || e.getCategory() == Biome.BiomeCategory.THEEND)
            return;

        addMobSpawns(e);


        if ((e.getCategory().equals(Biome.BiomeCategory.TAIGA) || e.getName().equals(new ResourceLocation(ArsNouveau.MODID, "archwood_forest")))  && Config.SPAWN_BERRIES.get()) {
            e.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, WorldEvent.BERRY_BUSH_PATCH_CONFIG).build();
        }

        if(Config.TREE_SPAWN_RATE.get() > 0)
            e.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, WorldEvent.PLACED_MIXED);

//        if(e.getName().equals(archwoodForest.getRegistryName())){
//            addArchwoodForestFeatures(e);
//        }
    }
    //
    public static void addMobSpawns(BiomeLoadingEvent e){
        List<Biome.BiomeCategory> categories = Arrays.asList(Biome.BiomeCategory.FOREST, Biome.BiomeCategory.EXTREME_HILLS, Biome.BiomeCategory.JUNGLE, Biome.BiomeCategory.PLAINS, Biome.BiomeCategory.SWAMP, Biome.BiomeCategory.SAVANNA, Biome.BiomeCategory.MOUNTAIN);

        if (categories.contains(e.getCategory())) {
            if (Config.CARBUNCLE_WEIGHT.get() > 0) {
                e.getSpawns().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.STARBUNCLE_TYPE, Config.CARBUNCLE_WEIGHT.get(), 1, 1));
            }
            if (Config.SYLPH_WEIGHT.get() > 0) {
                e.getSpawns().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.WHIRLISPRIG_TYPE, Config.SYLPH_WEIGHT.get(), 1, 1));
            }
        }
        if (Config.DRYGMY_WEIGHT.get() > 0) {
            e.getSpawns().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.ENTITY_DRYGMY, Config.DRYGMY_WEIGHT.get(), 1, 1));
        }

        if(!e.getCategory().equals(Biome.BiomeCategory.MUSHROOM) && !e.getCategory().equals(Biome.BiomeCategory.NONE)){
            if(e.getClimate().temperature <= 0.35f &&  Config.WGUARDIAN_WEIGHT.get() > 0){
                e.getSpawns().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.WILDEN_GUARDIAN, Config.WGUARDIAN_WEIGHT.get(), 1, 1));
            }
            if( Config.WSTALKER_WEIGHT.get() > 0)
                e.getSpawns().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.WILDEN_STALKER, Config.WSTALKER_WEIGHT.get(), 3, 3));
            if( Config.WHUNTER_WEIGHT.get() > 0)
                e.getSpawns().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.WILDEN_HUNTER, Config.WHUNTER_WEIGHT.get(), 1, 1));
        }
    }
}
