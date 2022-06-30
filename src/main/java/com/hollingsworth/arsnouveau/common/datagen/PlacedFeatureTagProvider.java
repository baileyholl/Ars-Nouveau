package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.world.WorldEvent;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class PlacedFeatureTagProvider extends TagsProvider<PlacedFeature> {
    public static TagKey<PlacedFeature> ARCHWOOD_TREES = TagKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation(ArsNouveau.MODID, "archwood_trees"));

    public PlacedFeatureTagProvider(DataGenerator p_211094_, String modId, @org.jetbrains.annotations.Nullable net.minecraftforge.common.data.ExistingFileHelper existingFileHelper) {
        super(p_211094_, BuiltinRegistries.PLACED_FEATURE, modId, existingFileHelper);
    }

    protected void addTags() {
        this.tag(ARCHWOOD_TREES).add(WorldEvent.PLACED_MIXED.get());
    }
}
