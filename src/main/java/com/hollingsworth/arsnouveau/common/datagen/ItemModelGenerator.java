package com.hollingsworth.arsnouveau.common.datagen;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

// ItemModelProvider was removed from NeoForge 21.11 - datagen stubbed until reimplemented
public class ItemModelGenerator implements DataProvider {

    public ItemModelGenerator(PackOutput output) {}

    // Legacy constructor called from ModDatagen with ExistingFileHelper - ignored
    public ItemModelGenerator(PackOutput output, Object existingFileHelper) {}

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return CompletableFuture.allOf();
    }

    @Override
    public String getName() {
        return "Item Models";
    }
}
