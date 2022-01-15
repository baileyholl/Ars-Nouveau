package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectBreak;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GlyphRecipeProvider implements DataProvider {
    private final DataGenerator generator;
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private static final Logger LOGGER = LogManager.getLogger();
    public GlyphRecipeProvider(DataGenerator generatorIn) {
        this.generator = generatorIn;
    }
    @Override
    public void run(HashCache cache) throws IOException {
        List<Glyph> glyphList = ArsNouveauAPI.getInstance().getGlyphItemMap().values().stream().collect(Collectors.toList());
        Path output = this.generator.getOutputFolder();
        for(Glyph g : glyphList){
            Path path = getGlyphPath(output, g);
            DataProvider.save(GSON, cache, g.asRecipe(), path);
        }
        List<GlyphRecipe> recipes = new ArrayList<>();
        recipes.add(new GlyphRecipe(new ResourceLocation(ArsNouveau.MODID, "glyph_break"), ArsNouveauAPI.getInstance().getGlyphItem(EffectBreak.INSTANCE).getDefaultInstance(), new ArrayList<>(), 2)
                .withIngredient(Ingredient.of(Items.DIRT)).withIngredient(Ingredient.of(Items.AMETHYST_BLOCK)));
        for(GlyphRecipe recipe : recipes){
            Path path = getScribeGlyphPath(output,  recipe.output.getItem());
            DataProvider.save(GSON, cache, recipe.asRecipe(), path);
        }

    }

    private static Path getGlyphPath(Path pathIn, Glyph glyph) {
        return pathIn.resolve("data/ars_nouveau/recipes/glyphs/" + glyph.getRegistryName().getPath() + ".json");
    }
    private static Path getScribeGlyphPath(Path pathIn, Item glyph) {
        return pathIn.resolve("data/ars_nouveau/recipes/" + glyph.getRegistryName().getPath() + ".json");
    }
    @Override
    public String getName() {
        return "Glyph Recipes";
    }
}
