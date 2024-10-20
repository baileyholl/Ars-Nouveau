package com.hollingsworth.arsnouveau.common.datagen;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.registries.RegistryPatchGenerator;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public abstract class SimpleDataProvider implements DataProvider {
    public ImmutableList.Builder<CompletableFuture<?>> futuresBuilder = new ImmutableList.Builder<>();
    public Path output;
    public final DataGenerator generator;
    CompletableFuture<HolderLookup.Provider> registries = CompletableFuture.supplyAsync(() -> null);
    CompletableFuture<HolderLookup.Provider> patchedRegistries = CompletableFuture.supplyAsync(() -> null);
    public SimpleDataProvider(DataGenerator dataGenerator){
        this.generator = dataGenerator;
        this.output = dataGenerator.getPackOutput().getOutputFolder();
    }

    public SimpleDataProvider(DataGenerator dataGenerator, CompletableFuture<HolderLookup.Provider> registries, RegistrySetBuilder setBuilder){
        this(dataGenerator);
        var patchedLookup = RegistryPatchGenerator.createLookup(registries, setBuilder);
        this.registries = RegistryPatchGenerator.createLookup(registries, setBuilder).thenApply(RegistrySetBuilder.PatchedRegistries::patches);
        patchedRegistries = patchedLookup.thenApply(RegistrySetBuilder.PatchedRegistries::full);
    }

    // Save stable to jsons
    public void collectJsons(CachedOutput pOutput){

    }

    public void collectJsons(CachedOutput output, HolderLookup.Provider provider){
        collectJsons(output);
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput pOutput) {
        return this.patchedRegistries.thenCompose((lookup) -> {
            collectJsons(pOutput, lookup);
            return CompletableFuture.allOf(futuresBuilder.build().toArray(CompletableFuture[]::new));
        });
    }

    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput pOutput, HolderLookup.Provider provider) {
        collectJsons(pOutput);
        return CompletableFuture.allOf(futuresBuilder.build().toArray(CompletableFuture[]::new));
    }

    public void saveStable(CachedOutput pOutput, JsonElement jsonElement, Path path){
        futuresBuilder.add(DataProvider.saveStable(pOutput, jsonElement, path));
    }
//    @Override
//    public CompletableFuture<?> run(CachedOutput output) {
//        return this.registries
//                .thenCompose(
//                        p_326736_ -> {
//                            DynamicOps<JsonElement> dynamicops = p_326736_.createSerializationContext(JsonOps.INSTANCE);
//                            return CompletableFuture.allOf(
//                                    net.neoforged.neoforge.registries.DataPackRegistriesHooks.getDataPackRegistriesWithDimensions()
//                                            .flatMap(
//                                                    p_256552_ -> this.dumpRegistryCap(output, p_326736_, dynamicops, (RegistryDataLoader.RegistryData<?>)p_256552_).stream()
//                                            )
//                                            .toArray(CompletableFuture[]::new)
//                            );
//                        }
//                );
//    }
//
//    private <T> Optional<CompletableFuture<?>> dumpRegistryCap(
//            CachedOutput output, HolderLookup.Provider registries, DynamicOps<JsonElement> ops, RegistryDataLoader.RegistryData<T> registryData
//    ) {
//        ResourceKey<? extends Registry<T>> resourcekey = registryData.key();
//        var conditionalCodec = net.neoforged.neoforge.common.conditions.ConditionalOps.createConditionalCodecWithConditions(registryData.elementCodec());
//        return registries.lookup(resourcekey)
//                .map(
//                        p_349921_ -> {
//                            PackOutput.PathProvider packoutput$pathprovider = this.output.createRegistryElementsPathProvider(resourcekey);
//                            return CompletableFuture.allOf(
//                                    p_349921_.listElements()
//                                            .map(
//                                                    p_256105_ -> dumpValue(
//                                                            packoutput$pathprovider.json(p_256105_.key().location()),
//                                                            output,
//                                                            ops,
//                                                            conditionalCodec,
//                                                            Optional.of(new net.neoforged.neoforge.common.conditions.WithConditions<>(conditions.getOrDefault(p_256105_.key(), java.util.List.of()), p_256105_.value()))
//                                                    )
//                                            )
//                                            .toArray(CompletableFuture[]::new)
//                            );
//                        }
//                );
//    }
//
//    private static <E> CompletableFuture<?> dumpValue(
//            Path p_255678_, CachedOutput p_256438_, DynamicOps<JsonElement> p_256127_, Encoder<Optional<WithConditions<E>>> p_255938_, java.util.Optional<net.neoforged.neoforge.common.conditions.WithConditions<E>> p_256590_
//    ) {
//        return p_255938_.encodeStart(p_256127_, p_256590_)
//                .mapOrElse(
//                        p_351699_ -> DataProvider.saveStable(p_256438_, p_351699_, p_255678_),
//                        p_351701_ -> CompletableFuture.failedFuture(new IllegalStateException("Couldn't generate file '" + p_255678_ + "': " + p_351701_.message()))
//                );
//    }
}
