package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.StructureTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class StructureTagProvider extends StructureTagsProvider {

    public static TagKey<Structure> WILDEN_DEN = TagKey.create(Registry.STRUCTURE_REGISTRY, new ResourceLocation(ArsNouveau.MODID, "wilden_den"));

    public static final ResourceKey<Structure> HUNTER_DEN = register("hunter_wilden_den");
    public static final ResourceKey<Structure> STALKER_DEN = register("stalker_wilden_den");
    public static final ResourceKey<Structure> GUARDIAN_DEN = register("guardian_wilden_den");

    public StructureTagProvider(DataGenerator pGenerator, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(WILDEN_DEN).add(HUNTER_DEN, STALKER_DEN, GUARDIAN_DEN);
    }

    public static ResourceKey<Structure> register(String name) {
        return ResourceKey.create(Registry.STRUCTURE_REGISTRY, new ResourceLocation(ArsNouveau.MODID, name));
    }
}
