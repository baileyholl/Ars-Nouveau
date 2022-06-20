package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.world.biome.ModBiomes;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.tags.BiomeTags.*;

public class BiomeTagProvider extends BiomeTagsProvider {
    public static TagKey<Biome> SUMMON_SPAWN_TAG = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(ArsNouveau.MODID, "summon_spawn"));

    public BiomeTagProvider(DataGenerator p_211094_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_211094_, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
        addTagsToBiome(ModBiomes.ARCHWOOD_FOREST, IS_FOREST, IS_OVERWORLD, HAS_VILLAGE_PLAINS);
        
        this.tag(SUMMON_SPAWN_TAG).addTags(BiomeTags.IS_FOREST, BiomeTags.IS_HILL, IS_TAIGA, IS_JUNGLE, IS_SAVANNA, IS_OVERWORLD ).add(Biomes.PLAINS);
        
    }
    
    void addTagsToBiome(ResourceKey<Biome> biome, TagKey<Biome>... tags){
        
        for (TagKey<Biome> tag : tags){
            this.tag(tag).add(biome);
        }
        
    }
    
}
