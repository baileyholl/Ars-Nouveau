package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.api.recipe.SummonRitualRecipe;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SummonRitualProvider implements DataProvider{

    public List<SummonRitualRecipe> recipes = new ArrayList<>();
    public final DataGenerator generator;


    public SummonRitualProvider(DataGenerator generatorIn) {
        this.generator = generatorIn;
    }
    @Override
    public void run(CachedOutput cache) throws IOException {
        addEntries();
        Path output = this.generator.getOutputFolder();
        for (SummonRitualRecipe recipe : recipes) {
                Path path = getRecipePath(output, recipe.getId().getPath());
                DataProvider.saveStable(cache, recipe.asRecipe(), path);
            }
        }

    protected void addEntries() {
    }

    protected static Path getRecipePath(Path path, String id) {
        return path.resolve("data/ars_nouveau/recipes/summon_ritual/" + id + ".json");
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return "Summon Ritual Datagen";
    }
}
