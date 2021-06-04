package com.hollingsworth.arsnouveau.common.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.common.block.tile.LightTile;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.world.feature.SingleBlockFeature;
import com.hollingsworth.arsnouveau.common.world.tree.MagicTrunkPlacer;
import com.hollingsworth.arsnouveau.common.world.tree.SupplierBlockStateProvider;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.block.Blocks;
import net.minecraft.block.PaneBlock;
import net.minecraft.entity.EntityClassification;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeMaker;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.template.*;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.hollingsworth.arsnouveau.common.world.FeatureLib.*;
import static net.minecraft.world.gen.feature.Features.*;

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

    public static final StructureProcessorList ARCHWOOD_PLAINS =
            new StructureProcessorList(ImmutableList.of(new RuleStructureProcessor(
                    ImmutableList.of(new RuleEntry(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.8F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.defaultBlockState()),
                            new RuleEntry(new TagMatchRuleTest(BlockTags.DOORS), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.defaultBlockState()),
                            new RuleEntry(new BlockMatchRuleTest(Blocks.TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.defaultBlockState()),
                            new RuleEntry(new BlockMatchRuleTest(Blocks.WALL_TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.defaultBlockState()),
                            new RuleEntry(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.07F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.defaultBlockState()),
                            new RuleEntry(new RandomBlockMatchRuleTest(Blocks.MOSSY_COBBLESTONE, 0.07F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.defaultBlockState()),
                            new RuleEntry(new RandomBlockMatchRuleTest(Blocks.WHITE_TERRACOTTA, 0.07F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.defaultBlockState()),
                            new RuleEntry(new RandomBlockMatchRuleTest(Blocks.OAK_LOG, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.defaultBlockState()),
                            new RuleEntry(new RandomBlockMatchRuleTest(Blocks.OAK_PLANKS, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.defaultBlockState()),
                            new RuleEntry(new RandomBlockMatchRuleTest(Blocks.OAK_STAIRS, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.defaultBlockState()),
                            new RuleEntry(new RandomBlockMatchRuleTest(Blocks.STRIPPED_OAK_LOG, 0.02F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.defaultBlockState()),
                            new RuleEntry(new BlockStateMatchRuleTest(Blocks.GLASS_PANE.defaultBlockState().setValue(PaneBlock.NORTH, Boolean.valueOf(true)).setValue(PaneBlock.SOUTH, Boolean.valueOf(true))), AlwaysTrueRuleTest.INSTANCE, Blocks.BROWN_STAINED_GLASS_PANE.defaultBlockState().setValue(PaneBlock.NORTH, Boolean.valueOf(true)).setValue(PaneBlock.SOUTH, Boolean.valueOf(true))),
                            new RuleEntry(new BlockStateMatchRuleTest(Blocks.GLASS_PANE.defaultBlockState().setValue(PaneBlock.EAST, Boolean.valueOf(true)).setValue(PaneBlock.WEST, Boolean.valueOf(true))), AlwaysTrueRuleTest.INSTANCE, Blocks.BROWN_STAINED_GLASS_PANE.defaultBlockState().setValue(PaneBlock.EAST, Boolean.valueOf(true)).setValue(PaneBlock.WEST, Boolean.valueOf(true))),
                            new RuleEntry(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.CARROTS.defaultBlockState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.POTATOES.defaultBlockState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.BEETROOTS.defaultBlockState())))));



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


        ConfiguredFeature<?, ?> FLOURISHING_COMMON = FLOURISHING_TREE
                .decorated(Features.Placements.HEIGHTMAP_SQUARE)
                .decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(12, 0.01f, 1)));
        ConfiguredFeature<?, ?> CASCADE_COMMON = CASCADING_TREE
                .decorated(Features.Placements.HEIGHTMAP_SQUARE)
                .decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(12, 0.01f, 1)));
        ConfiguredFeature<?, ?> BLAZE_COMMON = BLAZING_TREE
                .decorated(Features.Placements.HEIGHTMAP_SQUARE)
                .decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(12, 0.01f, 1)));
        ConfiguredFeature<?, ?> VEX_COMMON = VEXING_TREE
                .decorated(Features.Placements.HEIGHTMAP_SQUARE)
                .decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(12, 0.01f, 1)));

        ConfiguredFeature<?, ?> BLAZE_SEMI = BLAZING_TREE
                .decorated(Features.Placements.HEIGHTMAP_SQUARE)
                .decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(1, 0.01f, 1)));


        Feature<BlockStateFeatureConfig> LIGHTS = new SingleBlockFeature(BlockStateFeatureConfig.CODEC) {


            public void onStatePlace(ISeedReader seed, ChunkGenerator chunkGenerator, Random rand, BlockPos pos, BlockStateFeatureConfig config) {
                if(seed instanceof WorldGenRegion){
                    WorldGenRegion world = (WorldGenRegion) seed;
                    Random random = world.getRandom();
                    if(world.getBlockEntity(pos) instanceof LightTile){
                        LightTile tile = (LightTile) world.getBlockEntity(pos);
                        tile.red = Math.max(10, random.nextInt(255));
                        tile.green = Math.max(10, random.nextInt(255));
                        tile.blue = Math.max(10, random.nextInt(255));
                    }
                }
            }
        };

        ConfiguredFeature<?, ?> RANDOM_LIGHTS = LIGHTS.configured(new BlockStateFeatureConfig(BlockRegistry.LIGHT_BLOCK.defaultBlockState()))
                .decorated(Features.Placements.HEIGHTMAP_SQUARE)
                .decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(0, 0.5f, 1)));



        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, BlockRegistry.ARCANE_ORE.getRegistryName(),
                Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE,
                        BlockRegistry.ARCANE_ORE.defaultBlockState(), 5)).range(60).squared().count(5));

        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, BlockRegistry.MANA_BERRY_BUSH.getRegistryName(),
                Feature.RANDOM_PATCH.configured(BERRY_BUSH_PATCH_CONFIG).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE));

        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, BlockRegistry.VEXING_SAPLING.getRegistryName(), VEX);
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, BlockRegistry.BLAZING_SAPLING.getRegistryName(), BLAZE);
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, BlockRegistry.CASCADING_SAPLING.getRegistryName(), CASCADE);
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, BlockRegistry.FLOURISHING_SAPLING.getRegistryName(), FLOURISHING);
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, FeatureLib.RANDOM_LIGHTS_LOC, RANDOM_LIGHTS);

        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, FeatureLib.BLAZE_COMMON_LOC, BLAZE_COMMON);
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, FeatureLib.BLAZE_SEMI_LOC, BLAZE_SEMI);
//        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, BlockRegistry.VEXING_SAPLING.getRegistryName(), VEX_COMMON);
//        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, BlockRegistry.CASCADING_SAPLING.getRegistryName(), CASCADE_COMMON);
//        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, BlockRegistry.FLOURISHING_SAPLING.getRegistryName(), FLOURISHING_COMMON);

     //   Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(ArsNouveau.MODID, "archwood_trees"), FLOURISHING_COMMON);
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, ARCHWOOD_TREES, Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(
                VEX_COMMON.weighted(0.15f),
                BLAZE_COMMON.weighted(0.15f),
                CASCADE_COMMON.weighted(0.15f),
                FLOURISHING_COMMON.weighted(0.15f)
                ),VEXING_TREE
                .decorated(Features.Placements.HEIGHTMAP_SQUARE)
                .decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(0, 0, 0))))));

        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, VANILLA_BIG_TREES, Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(
                FANCY_OAK.weighted(0.5f),
                FANCY_OAK_BEES_0002.weighted(0.2f),
                FANCY_OAK_BEES_005.weighted(0.2f),
                DARK_OAK.weighted(0.1f)
        ),VEXING_TREE
                .decorated(Features.Placements.HEIGHTMAP_SQUARE)
                .decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(0, 0, 0))))));


        Registry.register(WorldGenRegistries.PROCESSOR_LIST, new ResourceLocation(ArsNouveau.MODID, "archwood_plains"), ARCHWOOD_PLAINS);

    }
//        "sky_color": 7978751,
//                "fog_color": 12635903,
//                "water_color": 4164351,
//                "water_fog_color": 329011,
//                "grass_color": 2031484


    @SubscribeEvent
    public static void biomeLoad(BiomeLoadingEvent e) {
        if (e.getCategory() == Biome.Category.NETHER || e.getCategory() == Biome.Category.THEEND)
            return;
        if (Config.SPAWN_ORE.get()) {
            e.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                    Objects.requireNonNull(WorldGenRegistries.CONFIGURED_FEATURE.get(BlockRegistry.ARCANE_ORE.getRegistryName()))).build();
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

        if ((e.getCategory().equals(Biome.Category.TAIGA) ||e.getName().equals(new ResourceLocation(ArsNouveau.MODID, "archwood_forest")))  && Config.SPAWN_BERRIES.get()) {
            e.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Objects.requireNonNull(WorldGenRegistries.CONFIGURED_FEATURE.get(BlockRegistry.MANA_BERRY_BUSH.getRegistryName()))).build();
        }
        //Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(MAGIC_TREE_CONFIG.withChance(0.2F)), MAGIC_TREE_CONFIG)),
        e.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
                Objects.requireNonNull(WorldGenRegistries.CONFIGURED_FEATURE.get(BlockRegistry.VEXING_SAPLING.getRegistryName()))).build();

        e.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
                Objects.requireNonNull(WorldGenRegistries.CONFIGURED_FEATURE.get(BlockRegistry.CASCADING_SAPLING.getRegistryName()))).build();

        e.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
                Objects.requireNonNull(WorldGenRegistries.CONFIGURED_FEATURE.get(BlockRegistry.BLAZING_SAPLING.getRegistryName()))).build();

        e.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
                Objects.requireNonNull(WorldGenRegistries.CONFIGURED_FEATURE.get(BlockRegistry.FLOURISHING_SAPLING.getRegistryName()))).build();

        if(e.getName().equals(archwoodForest.getRegistryName())){
            addArchwoodForestFeatures(e);
        }
        if(e.getName().equals(blazingForest.getRegistryName())){
            addBlazingForestFeatures(e);

        }
    }

    public static void addBlazingForestFeatures(BiomeLoadingEvent e){
        e.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
                Objects.requireNonNull(WorldGenRegistries.CONFIGURED_FEATURE.get(BLAZE_SEMI_LOC))).build();

    }

    public static void addArchwoodForestFeatures(BiomeLoadingEvent e){
        e.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
                Objects.requireNonNull(WorldGenRegistries.CONFIGURED_FEATURE.get(ARCHWOOD_TREES))).build();
        e.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
                Objects.requireNonNull(WorldGenRegistries.CONFIGURED_FEATURE.get(VANILLA_BIG_TREES))).build();
        e.getGeneration().addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS,
                Objects.requireNonNull(WorldGenRegistries.CONFIGURED_FEATURE.get(RANDOM_LIGHTS_LOC))).build();

//        e.getSpawns().addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(ModEntities.ENTITY_CARBUNCLE_TYPE,20, 2, 4));
//        e.getSpawns().addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(ModEntities.ENTITY_SYLPH_TYPE, 20, 2, 4));
    }

    public static Biome archwoodForest = BiomeMaker.theVoidBiome().setRegistryName(ArsNouveau.MODID, "archwood_forest");
    public static Biome blazingForest = BiomeMaker.theVoidBiome().setRegistryName(ArsNouveau.MODID, "blazing_archwood_forest");
    public static RegistryKey<Biome> archwoodKey = BiomeRegistry.k(archwoodForest);

    @ObjectHolder(ArsNouveau.MODID)
    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class BiomeRegistry{
        @SubscribeEvent
        public static void biomeRegistry(final RegistryEvent.Register<Biome> biomeRegistryEvent) {
            biomeRegistryEvent.getRegistry().registerAll(archwoodForest);
            BiomeDictionary.addTypes(archwoodKey, BiomeDictionary.Type.OVERWORLD);
        }

        private static RegistryKey<Biome> k(Biome b) {
            return RegistryKey.create(Registry.BIOME_REGISTRY, Objects.requireNonNull(b.getRegistryName()));
        }
    }



    @SubscribeEvent
    public static void worldTick(TickEvent.ServerTickEvent e) {

        if (e.side != LogicalSide.SERVER || e.phase != TickEvent.Phase.END)
            return;

        EventQueue.getInstance().tick();
    }


}
