package com.hollingsworth.arsnouveau.common.datagen;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

// BlockStateProvider was removed from NeoForge 21.11 - datagen stubbed until reimplemented
public class BlockStatesDatagen implements DataProvider {

    public BlockStatesDatagen(PackOutput output, String modid) {}

    // Legacy constructor called from ModDatagen with ExistingFileHelper - ignored
    public BlockStatesDatagen(PackOutput output, String modid, Object exFileHelper) {}

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return CompletableFuture.allOf();
    }

    @Override
    public String getName() {
        return "Block States";
    }

    // Stub so ItemModelGenerator can still call this without NPE
    public static Object getUncheckedModel(String registry) {
        return null;
    }
}
