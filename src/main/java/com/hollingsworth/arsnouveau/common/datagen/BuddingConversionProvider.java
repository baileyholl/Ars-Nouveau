package com.hollingsworth.arsnouveau.common.datagen;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.recipe.BuddingConversionRecipe;
import com.hollingsworth.arsnouveau.api.recipe.SummonRitualRecipe;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BuddingConversionProvider extends SimpleDataProvider{

    public List<BuddingConversionRecipe> recipes = new ArrayList<>();

    public BuddingConversionProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addEntries();
        for (BuddingConversionRecipe recipe : recipes) {
            Path path = getRecipePath(output, recipe.getId().getPath());
            saveStable(pOutput, recipe.asRecipe(), path);
        }
    }

    protected void addEntries() {
        recipes.add(new BuddingConversionRecipe(new ResourceLocation(ArsNouveau.MODID, "budding_amethyst"), Blocks.AMETHYST_BLOCK, Blocks.BUDDING_AMETHYST));
    }

    protected static Path getRecipePath(Path path, String id) {
        return path.resolve("data/ars_nouveau/recipes/budding_conversion/" + id + ".json");
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return "Budding Conversion Datagen";
    }
}
