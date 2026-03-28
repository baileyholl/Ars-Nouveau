package com.hollingsworth.arsnouveau.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AdvancementProvider extends net.minecraft.data.advancements.AdvancementProvider {

    public AdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, List.of(new com.hollingsworth.arsnouveau.common.datagen.advancement.ANAdvancements()));
    }
}
