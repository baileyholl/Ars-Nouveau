package com.hollingsworth.arsnouveau.common.datagen;


import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ANAdvancements extends ForgeAdvancementProvider {

    public ANAdvancements(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new com.hollingsworth.arsnouveau.common.datagen.advancement.ANAdvancements()));
    }
}
