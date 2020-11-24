package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class WorldEvent {

    @SubscribeEvent
    public static void biomeLoad(BiomeLoadingEvent e) {
        if(e.getCategory() == Biome.Category.NETHER || e.getCategory() == Biome.Category.THEEND)
            return;
        if(Config.SPAWN_ORE.get()){
            e.getGeneration().withFeature( GenerationStage.Decoration.UNDERGROUND_ORES,
                    WorldGenRegistries.CONFIGURED_FEATURE.getOrDefault(BlockRegistry.ARCANE_ORE.getRegistryName())).build();
        }
        List<Biome.Category> categories = Arrays.asList(Biome.Category.FOREST, Biome.Category.EXTREME_HILLS, Biome.Category.JUNGLE,
                Biome.Category.PLAINS, Biome.Category.SWAMP, Biome.Category.SAVANNA);
        if(categories.contains(e.getCategory())) {
            e.getSpawns().withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(ModEntities.ENTITY_CARBUNCLE_TYPE, Config.CARBUNCLE_WEIGHT.get(), 1, 1));
            e.getSpawns().withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(ModEntities.ENTITY_SYLPH_TYPE, Config.SYLPH_WEIGHT.get(), 1, 1));
        }
    }

    @SubscribeEvent
    public static void worldTick(TickEvent.WorldTickEvent e){
        World world = e.world;
        if(world.isRemote)
            return;
        EventQueue.getInstance().tick();
    }
}
