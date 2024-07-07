package com.hollingsworth.arsnouveau.common.datagen;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.crafting.recipes.BuddingConversionRecipe;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BuddingConversionProvider extends SimpleDataProvider{

    public List<Wrapper> recipes = new ArrayList<>();

    public BuddingConversionProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addEntries();
        for (Wrapper recipe : recipes) {
            Path path = getRecipePath(output, recipe.location().getPath());
            saveStable(pOutput, ANCodecs.toJson(BuddingConversionRecipe.Serializer.CODEC.codec(), recipe.recipe), path);
        }
    }

    protected void addEntries() {
        recipes.add(new Wrapper(ArsNouveau.prefix( "budding_amethyst"), new BuddingConversionRecipe(Blocks.AMETHYST_BLOCK, Blocks.BUDDING_AMETHYST)));
    }

    public record Wrapper(ResourceLocation location, BuddingConversionRecipe recipe){

    }

    protected static Path getRecipePath(Path path, String id) {
        return path.resolve("data/ars_nouveau/recipe/budding_conversion/" + id + ".json");
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return "Budding Conversion Datagen";
    }
}
