package com.hollingsworth.arsnouveau.common.datagen;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public abstract class SimpleDataProvider implements DataProvider {
    public ImmutableList.Builder<CompletableFuture<?>> futuresBuilder = new ImmutableList.Builder<>();
    public Path output;
    public final DataGenerator generator;
    public SimpleDataProvider(DataGenerator dataGenerator){
        this.generator = dataGenerator;
        this.output = dataGenerator.getPackOutput().getOutputFolder();
    }

    // Save stable to jsons
    public abstract void collectJsons(CachedOutput pOutput);

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput pOutput) {
        collectJsons(pOutput);
        return CompletableFuture.allOf(futuresBuilder.build().toArray(CompletableFuture[]::new));
    }

    public void saveStable(CachedOutput pOutput, JsonElement jsonElement, Path path){
        futuresBuilder.add(DataProvider.saveStable(pOutput, jsonElement, path));
    }

}
