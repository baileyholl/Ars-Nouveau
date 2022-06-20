package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.JsonElement;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.setup.Config;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderSet;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import java.util.Map;
import java.util.function.BiConsumer;

public class BiomeModifierProvider<T extends BiomeModifier> extends JsonCodecProvider<BiomeModifier> {
    /**
     * @param dataGenerator      DataGenerator provided by {@link GatherDataEvent}.
     * @param existingFileHelper
     * @param modid
     * @param dynamicOps         DynamicOps to encode values to jsons with using the provided Codec, e.g. {@link JsonOps.INSTANCE}.
     * @param packType           PackType specifying whether to generate entries in assets or data.
     * @param directory          String representing the directory to generate jsons in, e.g. "dimension" or "cheesemod/cheese".
     * @param codec              Codec to encode values to jsons with using the provided DynamicOps.
     * @param entries            Map of named entries to serialize to jsons. Paths for values are derived from the ResourceLocation's entryid:entrypath as specified above.
     */
    public BiomeModifierProvider(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper, String modid, DynamicOps<JsonElement> dynamicOps, PackType packType, String directory,Codec<BiomeModifier> codec, Map<ResourceLocation, BiomeModifier> entries) {
        super(dataGenerator, existingFileHelper, modid, dynamicOps, packType, directory, codec, entries);
    }

    @Override
    protected void gather(BiConsumer<ResourceLocation, BiomeModifier> consumer) {
        super.gather(consumer);
        HolderSet.Named<Biome> SUMMON_TAG = new HolderSet.Named<>(BuiltinRegistries.BIOME, BiomeTagProvider.SUMMON_SPAWN_TAG);
        SUMMON_TAG.stream().forEach(h -> System.out.println(h.get()));
        consumer.accept(new ResourceLocation(ArsNouveau.MODID, "starbuncle_spawn"), ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(SUMMON_TAG,
                new MobSpawnSettings.SpawnerData(ModEntities.STARBUNCLE_TYPE.get(), Config.CARBUNCLE_WEIGHT.get(), 1, 1)));
    }
}
