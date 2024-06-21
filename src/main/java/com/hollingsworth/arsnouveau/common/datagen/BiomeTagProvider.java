package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.registry.BiomeRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.tags.BiomeTags.*;

public class BiomeTagProvider  extends BiomeTagsProvider {
    public static TagKey<Biome> SUMMON_SPAWN_TAG = TagKey.create(Registries.BIOME, new ResourceLocation(ArsNouveau.MODID, "summon_spawn"));
    //for common, cluster archwood
    public static TagKey<Biome> ARCHWOOD_BIOME_TAG = TagKey.create(Registries.BIOME, new ResourceLocation(ArsNouveau.MODID, "archwood_biome"));

    public static TagKey<Biome> NO_MOB_SPAWN = TagKey.create(Registries.BIOME, new ResourceLocation(ArsNouveau.MODID, "no_mob_spawn"));


    public static TagKey<Biome> BERRY_SPAWN = TagKey.create(Registries.BIOME, new ResourceLocation(ArsNouveau.MODID, "berry_spawn"));

    public BiomeTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pProvider, ArsNouveau.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(ARCHWOOD_BIOME_TAG).add(BiomeRegistry.ARCHWOOD_FOREST);
        addTagToTags(ARCHWOOD_BIOME_TAG, IS_FOREST, IS_OVERWORLD, HAS_VILLAGE_PLAINS);
        this.tag(SUMMON_SPAWN_TAG).addTags(IS_OVERWORLD);
        this.tag(NO_MOB_SPAWN).addTags(Tags.Biomes.IS_MUSHROOM).add(Biomes.DEEP_DARK);

        addTagToTags(IS_TAIGA, BERRY_SPAWN);
        this.tag(BERRY_SPAWN).add(BiomeRegistry.ARCHWOOD_FOREST);

    }

    void addTagToTags(TagKey<Biome> biomeTag, TagKey<Biome>... tags) {
        for (TagKey<Biome> tag : tags) {
            this.tag(tag).addTag(biomeTag);
        }
    }
}
