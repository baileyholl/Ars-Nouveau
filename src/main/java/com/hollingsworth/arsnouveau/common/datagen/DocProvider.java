package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocCategory;
import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;
import com.hollingsworth.arsnouveau.setup.registry.Documentation;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DocProvider extends SimpleDataProvider {
    public CompletableFuture<HolderLookup.Provider> registries;
    List<DocCategory> categories = new ArrayList<>();

    public DocProvider(DataGenerator generatorIn, CompletableFuture<HolderLookup.Provider> registries) {
        super(generatorIn);
        this.registries = registries;
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        Documentation.initOnWorldReload();
        for (DocCategory category : DocumentationRegistry.getMainCategoryMap().values()) {
            if (category.id().getNamespace().equals(ArsNouveau.MODID)) {
                categories.add(category);
            }
        }
        for (DocCategory category : categories) {
            saveStable(pOutput, category.toJson(), output.resolve("data/" + category.id().getNamespace() + "/doc/" + category.id().getPath() + ".json"));
        }
    }

    @Override
    public String getName() {
        return "Doc provider";
    }
}
