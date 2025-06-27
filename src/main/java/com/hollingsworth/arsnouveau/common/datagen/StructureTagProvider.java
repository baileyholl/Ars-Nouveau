package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class StructureTagProvider extends TagsProvider<Structure> {

    public static TagKey<Structure> WILDEN_DEN = TagKey.create(Registries.STRUCTURE, ArsNouveau.prefix("wilden_den"));

    public static final ResourceKey<Structure> HUNTER_DEN = register("hunter_wilden_den");
    public static final ResourceKey<Structure> STALKER_DEN = register("stalker_wilden_den");
    public static final ResourceKey<Structure> GUARDIAN_DEN = register("guardian_wilden_den");

    public StructureTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, Registries.STRUCTURE, pProvider, ArsNouveau.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(WILDEN_DEN).add(HUNTER_DEN, STALKER_DEN, GUARDIAN_DEN);
    }

    public static ResourceKey<Structure> register(String name) {
        return ResourceKey.create(Registries.STRUCTURE, ArsNouveau.prefix(name));
    }
}
