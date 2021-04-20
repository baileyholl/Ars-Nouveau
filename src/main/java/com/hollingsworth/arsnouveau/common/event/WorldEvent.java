package com.hollingsworth.arsnouveau.common.event;

import com.google.common.collect.ImmutableSet;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.world.tree.MagicTrunkPlacer;
import com.hollingsworth.arsnouveau.common.world.tree.SupplierBlockStateProvider;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class WorldEvent {
    public static ConfiguredFeature<BaseTreeFeatureConfig, ?> CASCADING_TREE =  Feature.TREE.configured((
            new BaseTreeFeatureConfig.Builder(new SupplierBlockStateProvider(LibBlockNames.CASCADING_LOG),
                    new SupplierBlockStateProvider(LibBlockNames.CASCADING_LEAVES),
                    new BlobFoliagePlacer(FeatureSpread.fixed(0), FeatureSpread.fixed(0), 0),
                    new MagicTrunkPlacer(9, 1, 0),
                    new TwoLayerFeature(2, 0, 2))).ignoreVines().build());

    public static ConfiguredFeature<BaseTreeFeatureConfig, ?> BLAZING_TREE =  Feature.TREE.configured((
            new BaseTreeFeatureConfig.Builder(new SupplierBlockStateProvider(LibBlockNames.BLAZING_LOG),
                    new SupplierBlockStateProvider(LibBlockNames.BLAZING_LEAVES),
                    new BlobFoliagePlacer(FeatureSpread.fixed(0), FeatureSpread.fixed(0), 0),
                    new MagicTrunkPlacer(9, 1, 0),
                    new TwoLayerFeature(2, 0, 2))).ignoreVines().build());

    public static ConfiguredFeature<BaseTreeFeatureConfig, ?> FLOURISHING_TREE =  Feature.TREE.configured((
            new BaseTreeFeatureConfig.Builder(new SupplierBlockStateProvider(LibBlockNames.FLOURISHING_LOG),
                    new SupplierBlockStateProvider(LibBlockNames.FLOURISHING_LEAVES),
                    new BlobFoliagePlacer(FeatureSpread.fixed(0), FeatureSpread.fixed(0), 0),
                    new MagicTrunkPlacer(9, 1, 0),
                    new TwoLayerFeature(2, 0, 2))).ignoreVines().build());

    public static ConfiguredFeature<BaseTreeFeatureConfig, ?> VEXING_TREE =  Feature.TREE.configured((
            new BaseTreeFeatureConfig.Builder(new SupplierBlockStateProvider(LibBlockNames.VEXING_LOG),
                    new SupplierBlockStateProvider(LibBlockNames.VEXING_LEAVES),
                    new BlobFoliagePlacer(FeatureSpread.fixed(0), FeatureSpread.fixed(0), 0),
                    new MagicTrunkPlacer(9, 1, 0),
                    new TwoLayerFeature(2, 0, 2))).ignoreVines().build());


    public static ConfiguredFeature<BaseTreeFeatureConfig, ?> MAGIC_TREE_CONFIG2 = Feature.TREE.configured((
            new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Blocks.DARK_OAK_LOG.defaultBlockState()),
                    new SimpleBlockStateProvider(Blocks.DARK_OAK_LEAVES.defaultBlockState()),
                    new BlobFoliagePlacer(FeatureSpread.fixed(0), FeatureSpread.fixed(0), 0),
                    new MagicTrunkPlacer(9, 1, 0),
                    new TwoLayerFeature(2, 0, 2))).ignoreVines().build());


    public static void registerFeatures() {
        BlockClusterFeatureConfig BERRY_BUSH_PATCH_CONFIG = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(BlockRegistry.MANA_BERRY_BUSH.defaultBlockState()), SimpleBlockPlacer.INSTANCE)).tries(64).whitelist(ImmutableSet.of(Blocks.GRASS_BLOCK)).noProjection().build();

        float treeChance = Config.TREE_SPAWN_RATE.get().floatValue();
        ConfiguredFeature<?, ?> CASCADE = CASCADING_TREE
                .decorated(Features.Placements.HEIGHTMAP_SQUARE)
                .decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(0, treeChance, 1)));
        ConfiguredFeature<?, ?> BLAZE = BLAZING_TREE
                .decorated(Features.Placements.HEIGHTMAP_SQUARE)
                .decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(0, treeChance, 1)));
        ConfiguredFeature<?, ?> VEX = VEXING_TREE
                .decorated(Features.Placements.HEIGHTMAP_SQUARE)
                .decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(0, treeChance, 1)));
        ConfiguredFeature<?, ?> FLOURISHING = FLOURISHING_TREE
                .decorated(Features.Placements.HEIGHTMAP_SQUARE)
                .decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(0, treeChance, 1)));

        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, BlockRegistry.ARCANE_ORE.getRegistryName(),
                Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE,
                        BlockRegistry.ARCANE_ORE.defaultBlockState(), 5)).range(60).squared().count(5));

        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, BlockRegistry.MANA_BERRY_BUSH.getRegistryName(),
                Feature.RANDOM_PATCH.configured(BERRY_BUSH_PATCH_CONFIG).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE));

        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, BlockRegistry.VEXING_SAPLING.getRegistryName(), VEX);
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, BlockRegistry.BLAZING_SAPLING.getRegistryName(), BLAZE);
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, BlockRegistry.CASCADING_SAPLING.getRegistryName(), CASCADE);
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, BlockRegistry.FLOURISHING_SAPLING.getRegistryName(), FLOURISHING);
    }

    @SubscribeEvent
    public static void biomeLoad(BiomeLoadingEvent e) {
        if (e.getCategory() == Biome.Category.NETHER || e.getCategory() == Biome.Category.THEEND)
            return;
        if (Config.SPAWN_ORE.get()) {
            e.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                    WorldGenRegistries.CONFIGURED_FEATURE.get(BlockRegistry.ARCANE_ORE.getRegistryName())).build();
        }
        List<Biome.Category> categories = Arrays.asList(Biome.Category.FOREST, Biome.Category.EXTREME_HILLS, Biome.Category.JUNGLE,
                Biome.Category.PLAINS, Biome.Category.SWAMP, Biome.Category.SAVANNA);
        if (categories.contains(e.getCategory())) {
            e.getSpawns().addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(ModEntities.ENTITY_CARBUNCLE_TYPE, Config.CARBUNCLE_WEIGHT.get(), 1, 1));
            e.getSpawns().addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(ModEntities.ENTITY_SYLPH_TYPE, Config.SYLPH_WEIGHT.get(), 1, 1));
        }

        if(!e.getCategory().equals(Biome.Category.MUSHROOM)){
            if(e.getClimate().temperature <= 0.35f &&  Config.WGUARDIAN_WEIGHT.get() > 0){
                e.getSpawns().addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.WILDEN_GUARDIAN, Config.WGUARDIAN_WEIGHT.get(), 1, 1));
            }
            if( Config.WSTALKER_WEIGHT.get() > 0)
                e.getSpawns().addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.WILDEN_STALKER, Config.WSTALKER_WEIGHT.get(), 3, 3));
            if( Config.WHUNTER_WEIGHT.get() > 0)
                e.getSpawns().addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.WILDEN_HUNTER, Config.WHUNTER_WEIGHT.get(), 1, 1));
        }

        if (e.getCategory().equals(Biome.Category.TAIGA) && Config.SPAWN_BERRIES.get()) {
            e.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Objects.requireNonNull(WorldGenRegistries.CONFIGURED_FEATURE.get(BlockRegistry.MANA_BERRY_BUSH.getRegistryName()))).build();
        }
        //Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(MAGIC_TREE_CONFIG.withChance(0.2F)), MAGIC_TREE_CONFIG)),
        e.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
                WorldGenRegistries.CONFIGURED_FEATURE.get(BlockRegistry.VEXING_SAPLING.getRegistryName())).build();

        e.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
                WorldGenRegistries.CONFIGURED_FEATURE.get(BlockRegistry.CASCADING_SAPLING.getRegistryName())).build();

        e.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
                WorldGenRegistries.CONFIGURED_FEATURE.get(BlockRegistry.BLAZING_SAPLING.getRegistryName())).build();

        e.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
                WorldGenRegistries.CONFIGURED_FEATURE.get(BlockRegistry.FLOURISHING_SAPLING.getRegistryName())).build();

    }

    @SubscribeEvent
    public static void worldTick(TickEvent.ServerTickEvent e) {

        if (e.side != LogicalSide.SERVER || e.phase != TickEvent.Phase.END)
            return;

        EventQueue.getInstance().tick();
    }


}
