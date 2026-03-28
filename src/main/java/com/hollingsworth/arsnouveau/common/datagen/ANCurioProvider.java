package com.hollingsworth.arsnouveau.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

// Curios API not available for 1.21.11 yet - stubbed until dependency is available
public class ANCurioProvider implements DataProvider {

    public ANCurioProvider(PackOutput output, Object fileHelper, CompletableFuture<HolderLookup.Provider> registries) {}

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return CompletableFuture.allOf();
    }

    @Override
    public String getName() {
        return "AN Curio Provider";
    }
}
