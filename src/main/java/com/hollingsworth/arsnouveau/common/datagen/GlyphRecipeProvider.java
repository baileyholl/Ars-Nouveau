package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class GlyphRecipeProvider implements IDataProvider {
    private final DataGenerator generator;
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private static final Logger LOGGER = LogManager.getLogger();
    public GlyphRecipeProvider(DataGenerator generatorIn) {
        this.generator = generatorIn;
    }
    @Override
    public void act(DirectoryCache cache) throws IOException {
        List<Glyph> glyphList = ArsNouveauAPI.getInstance().getGlyphMap().values().stream().collect(Collectors.toList());
        Path output = this.generator.getOutputFolder();
        for(Glyph g : glyphList){
            Path path = getGlyphPath(output, g);
            IDataProvider.save(GSON, cache, g.asRecipe(), path);
        }
    }

    private static Path getGlyphPath(Path pathIn, Glyph glyph) {
        return pathIn.resolve("data/ars_nouveau/recipes/glyphs/" + glyph.getRegistryName().getPath() + ".json");
    }
    @Override
    public String getName() {
        return "Glyph Recipes";
    }
}
