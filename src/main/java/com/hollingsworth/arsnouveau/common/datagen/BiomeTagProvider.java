package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class BiomeTagProvider {
    public static TagKey<Biome> SUMMON_SPAWN_TAG = TagKey.create(Registries.BIOME, new ResourceLocation(ArsNouveau.MODID, "summon_spawn"));
    //for common, cluster archwood
    public static TagKey<Biome> ARCHWOOD_BIOME_TAG = TagKey.create(Registries.BIOME, new ResourceLocation(ArsNouveau.MODID, "archwood_biome"));

    public static TagKey<Biome> NO_MOB_SPAWN = TagKey.create(Registries.BIOME, new ResourceLocation(ArsNouveau.MODID, "no_mob_spawn"));


    public static TagKey<Biome> BERRY_SPAWN = TagKey.create(Registries.BIOME, new ResourceLocation(ArsNouveau.MODID, "berry_spawn"));

//    public BiomeTagProvider(DataGenerator p_211094_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
//        super(p_211094_, modId, existingFileHelper);
//    }
//
//    @Override
//    @SuppressWarnings("unchecked")
//    protected void addTags() {
//        addTagToTags(ARCHWOOD_BIOME_TAG, IS_FOREST, IS_OVERWORLD, HAS_VILLAGE_PLAINS);
//        this.tag(SUMMON_SPAWN_TAG).addTags(IS_OVERWORLD);
//        this.tag(ARCHWOOD_BIOME_TAG).add(ModBiomes.ARCHWOOD_FOREST);
//        this.tag(NO_MOB_SPAWN).addTags(Tags.Biomes.IS_MUSHROOM).add(Biomes.DEEP_DARK);
//
//        addTagToTags(IS_TAIGA, BERRY_SPAWN);
//        this.tag(BERRY_SPAWN).add(ModBiomes.ARCHWOOD_FOREST);
//
//    }
//
//    void addTagToTags(TagKey<Biome> biomeTag, TagKey<Biome>... tags) {
//        for (TagKey<Biome> tag : tags) {
//            this.tag(tag).addTag(biomeTag);
//        }
//    }
}
