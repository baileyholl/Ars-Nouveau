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
    public static ConfiguredFeature<BaseTreeFeatureConfig, ?> CASCADING_TREE =  Feature.TREE.withConfiguration((
            new BaseTreeFeatureConfig.Builder(new SupplierBlockStateProvider(LibBlockNames.CASCADING_LOG),
                    new SupplierBlockStateProvider(LibBlockNames.CASCADING_LEAVES),
                    new BlobFoliagePlacer(FeatureSpread.func_242252_a(0), FeatureSpread.func_242252_a(0), 0),
                    new MagicTrunkPlacer(9, 1, 0),
                    new TwoLayerFeature(2, 0, 2))).setIgnoreVines().build());

    public static ConfiguredFeature<BaseTreeFeatureConfig, ?> BLAZING_TREE =  Feature.TREE.withConfiguration((
            new BaseTreeFeatureConfig.Builder(new SupplierBlockStateProvider(LibBlockNames.BLAZING_LOG),
                    new SupplierBlockStateProvider(LibBlockNames.BLAZING_LEAVES),
                    new BlobFoliagePlacer(FeatureSpread.func_242252_a(0), FeatureSpread.func_242252_a(0), 0),
                    new MagicTrunkPlacer(9, 1, 0),
                    new TwoLayerFeature(2, 0, 2))).setIgnoreVines().build());

    public static ConfiguredFeature<BaseTreeFeatureConfig, ?> FLOURISHING_TREE =  Feature.TREE.withConfiguration((
            new BaseTreeFeatureConfig.Builder(new SupplierBlockStateProvider(LibBlockNames.FLOURISHING_LOG),
                    new SupplierBlockStateProvider(LibBlockNames.FLOURISHING_LEAVES),
                    new BlobFoliagePlacer(FeatureSpread.func_242252_a(0), FeatureSpread.func_242252_a(0), 0),
                    new MagicTrunkPlacer(9, 1, 0),
                    new TwoLayerFeature(2, 0, 2))).setIgnoreVines().build());

    public static ConfiguredFeature<BaseTreeFeatureConfig, ?> VEXING_TREE =  Feature.TREE.withConfiguration((
            new BaseTreeFeatureConfig.Builder(new SupplierBlockStateProvider(LibBlockNames.VEXING_LOG),
                    new SupplierBlockStateProvider(LibBlockNames.VEXING_LEAVES),
                    new BlobFoliagePlacer(FeatureSpread.func_242252_a(0), FeatureSpread.func_242252_a(0), 0),
                    new MagicTrunkPlacer(9, 1, 0),
                    new TwoLayerFeature(2, 0, 2))).setIgnoreVines().build());


    public static ConfiguredFeature<BaseTreeFeatureConfig, ?> MAGIC_TREE_CONFIG2 = Feature.TREE.withConfiguration((
            new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Blocks.DARK_OAK_LOG.getDefaultState()),
                    new SimpleBlockStateProvider(Blocks.DARK_OAK_LEAVES.getDefaultState()),
                    new BlobFoliagePlacer(FeatureSpread.func_242252_a(0), FeatureSpread.func_242252_a(0), 0),
                    new MagicTrunkPlacer(9, 1, 0),
                    new TwoLayerFeature(2, 0, 2))).setIgnoreVines().build());


    public static void registerFeatures() {
        BlockClusterFeatureConfig BERRY_BUSH_PATCH_CONFIG = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(BlockRegistry.MANA_BERRY_BUSH.getDefaultState()), SimpleBlockPlacer.PLACER)).tries(64).whitelist(ImmutableSet.of(Blocks.GRASS_BLOCK)).func_227317_b_().build();

        float treeChance = Config.TREE_SPAWN_RATE.get().floatValue();
        ConfiguredFeature<?, ?> CASCADE = CASCADING_TREE
                .withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
                .withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(0, treeChance, 1)));
        ConfiguredFeature<?, ?> BLAZE = BLAZING_TREE
                .withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
                .withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(0, treeChance, 1)));
        ConfiguredFeature<?, ?> VEX = VEXING_TREE
                .withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
                .withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(0, treeChance, 1)));
        ConfiguredFeature<?, ?> FLOURISHING = FLOURISHING_TREE
                .withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
                .withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(0, treeChance, 1)));

        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, BlockRegistry.ARCANE_ORE.getRegistryName(),
                Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD,
                        BlockRegistry.ARCANE_ORE.getDefaultState(), 5)).range(60).square().func_242731_b(5));

        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, BlockRegistry.MANA_BERRY_BUSH.getRegistryName(),
                Feature.RANDOM_PATCH.withConfiguration(BERRY_BUSH_PATCH_CONFIG).withPlacement(Features.Placements.PATCH_PLACEMENT));

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
            e.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                    WorldGenRegistries.CONFIGURED_FEATURE.getOrDefault(BlockRegistry.ARCANE_ORE.getRegistryName())).build();
        }
        List<Biome.Category> categories = Arrays.asList(Biome.Category.FOREST, Biome.Category.EXTREME_HILLS, Biome.Category.JUNGLE,
                Biome.Category.PLAINS, Biome.Category.SWAMP, Biome.Category.SAVANNA);
        if (categories.contains(e.getCategory())) {
            e.getSpawns().withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(ModEntities.ENTITY_CARBUNCLE_TYPE, Config.CARBUNCLE_WEIGHT.get(), 1, 1));
            e.getSpawns().withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(ModEntities.ENTITY_SYLPH_TYPE, Config.SYLPH_WEIGHT.get(), 1, 1));
        }

        if(!e.getCategory().equals(Biome.Category.MUSHROOM)){
            if(e.getClimate().temperature <= 0.35f &&  Config.WGUARDIAN_WEIGHT.get() > 0){
                e.getSpawns().withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.WILDEN_GUARDIAN, Config.WGUARDIAN_WEIGHT.get(), 1, 1));
            }
            if( Config.WSTALKER_WEIGHT.get() > 0)
                e.getSpawns().withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.WILDEN_STALKER, Config.WSTALKER_WEIGHT.get(), 3, 3));
            if( Config.WHUNTER_WEIGHT.get() > 0)
                e.getSpawns().withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.WILDEN_HUNTER, Config.WHUNTER_WEIGHT.get(), 1, 1));
        }

        if (e.getCategory().equals(Biome.Category.TAIGA) && Config.SPAWN_BERRIES.get()) {
            e.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Objects.requireNonNull(WorldGenRegistries.CONFIGURED_FEATURE.getOrDefault(BlockRegistry.MANA_BERRY_BUSH.getRegistryName()))).build();
        }
        //Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(MAGIC_TREE_CONFIG.withChance(0.2F)), MAGIC_TREE_CONFIG)),
        e.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
                WorldGenRegistries.CONFIGURED_FEATURE.getOrDefault(BlockRegistry.VEXING_SAPLING.getRegistryName())).build();

        e.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
                WorldGenRegistries.CONFIGURED_FEATURE.getOrDefault(BlockRegistry.CASCADING_SAPLING.getRegistryName())).build();

        e.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
                WorldGenRegistries.CONFIGURED_FEATURE.getOrDefault(BlockRegistry.BLAZING_SAPLING.getRegistryName())).build();

        e.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
                WorldGenRegistries.CONFIGURED_FEATURE.getOrDefault(BlockRegistry.FLOURISHING_SAPLING.getRegistryName())).build();

    }

    @SubscribeEvent
    public static void worldTick(TickEvent.ServerTickEvent e) {

        if (e.side != LogicalSide.SERVER || e.phase != TickEvent.Phase.END)
            return;

        EventQueue.getInstance().tick();
    }


}
