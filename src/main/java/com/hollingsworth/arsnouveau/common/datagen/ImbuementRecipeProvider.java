package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ImbuementRecipeProvider implements DataProvider {
    private final DataGenerator generator;
    List<ImbuementRecipe> recipes = new ArrayList<>();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private static final Logger LOGGER = LogManager.getLogger();

    public ImbuementRecipeProvider(DataGenerator generatorIn){
        this.generator = generatorIn;
    }

    @Override
    public void run(HashCache cache) throws IOException {
        recipes.add(new ImbuementRecipe("lapis", Ingredient.of(Tags.Items.GEMS_LAPIS), ItemsRegistry.SOURCE_GEM.getDefaultInstance(), 500));
        recipes.add(new ImbuementRecipe("amethyst", Ingredient.of(Items.AMETHYST_SHARD), ItemsRegistry.SOURCE_GEM.getDefaultInstance(), 500));
        recipes.add(new ImbuementRecipe("amethyst_block", Ingredient.of(Items.AMETHYST_SHARD), ItemsRegistry.SOURCE_GEM.getDefaultInstance(), 2000));

        Path output = this.generator.getOutputFolder();
        for(ImbuementRecipe g : recipes){
            Path path = getRecipePath(output, g.getId().getPath());
            DataProvider.save(GSON, cache,  g.asRecipe(), path);
        }
    }

    private static Path getRecipePath(Path pathIn, String str){
        return pathIn.resolve("data/ars_nouveau/recipes/" + str + ".json");
    }
    @Override
    public String getName() {
        return "Infuser";
    }
}
