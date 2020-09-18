package com.hollingsworth.arsnouveau.common.world;

import com.google.common.collect.Lists;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class FeatureGen {


    public static void setupOreGen() {
//        for (Biome biome : ForgeRegistries.BIOMES) {
////            biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(
////                    new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, BlockRegistry.ARCANE_ORE.getDefaultState(), 9))
////                    .withPlacement(Placement.COUNT_RANGE.configure(
////                    new CountRangeConfig(20,0,0,64))));
//
//        }
        initOres();
        setupOres();
    }


    public static void initOres() {
        System.out.println("Registered ore");
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, BlockRegistry.ARCANE_ORE.getRegistryName(),
                Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.field_241882_a,
                        BlockRegistry.ARCANE_ORE.getDefaultState(), 9)).func_242733_d(64).func_242728_a().func_242731_b(20));


    }

    public static void setupOres() {
//        for (Map.Entry<RegistryKey<Biome>, Biome> biome : WorldGenRegistries.BIOME.getEntries()){
//            if (!biome.getValue().getCategory().equals(Biome.Category.NETHER)
//                    && !biome.getValue().getCategory().equals(Biome.Category.THEEND)) {
//                addFeatureToBiome(biome.getValue(), GenerationStage.Decoration.UNDERGROUND_ORES,
//                        WorldGenRegistries.CONFIGURED_FEATURE.getOrDefault(BlockRegistry.ARCANE_ORE.getRegistryName()));
//            }
//
//        }
    }


    public static void addFeatureToBiome(Biome biome, GenerationStage.Decoration decoration,
                                         ConfiguredFeature<?, ?> configuredFeature) {
        List<List<Supplier<ConfiguredFeature<?, ?>>>> biomeFeatures = new ArrayList<>(
                biome.getGenerationSettings().getFeatures());
        while (biomeFeatures.size() <= decoration.ordinal()) {
            biomeFeatures.add(Lists.newArrayList());
        }
        List<Supplier<ConfiguredFeature<?, ?>>> features = new ArrayList<>(biomeFeatures.get(decoration.ordinal()));
        features.add(() -> configuredFeature);
        biomeFeatures.set(decoration.ordinal(), features);

        ObfuscationReflectionHelper.setPrivateValue(BiomeGenerationSettings.class, biome.getGenerationSettings(), biomeFeatures,
                "field_242484_f");
    }
}
