package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
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
    protected final DataGenerator generator;
    protected List<ImbuementRecipe> recipes = new ArrayList<>();
    protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private static final Logger LOGGER = LogManager.getLogger();

    public ImbuementRecipeProvider(DataGenerator generatorIn) {
        this.generator = generatorIn;
    }

    @Override
    public void run(CachedOutput cache) throws IOException {
        recipes.add(new ImbuementRecipe("lapis", Ingredient.of(Tags.Items.GEMS_LAPIS), ItemsRegistry.SOURCE_GEM.get().getDefaultInstance(), 500));
        recipes.add(new ImbuementRecipe("amethyst", Ingredient.of(Items.AMETHYST_SHARD), ItemsRegistry.SOURCE_GEM.get().getDefaultInstance(), 500));
        recipes.add(new ImbuementRecipe("amethyst_block", Ingredient.of(Items.AMETHYST_BLOCK), new ItemStack(BlockRegistry.SOURCE_GEM_BLOCK), 2000));
        recipes.add(new ImbuementRecipe("fire_essence", Recipes.SOURCE_GEM, new ItemStack(ItemsRegistry.FIRE_ESSENCE.get()), 2000)
                .withPedestalItem(Items.FLINT_AND_STEEL)
                .withPedestalItem(Items.TORCH).withPedestalItem(Items.GUNPOWDER));
        recipes.add(new ImbuementRecipe("air_essence", Recipes.SOURCE_GEM, new ItemStack(ItemsRegistry.AIR_ESSENCE.get()), 2000)
                .withPedestalItem(Items.FEATHER)
                .withPedestalItem(ItemsRegistry.WILDEN_WING)
                .withPedestalItem(Ingredient.of(ItemTags.ARROWS)));
        recipes.add(new ImbuementRecipe("earth_essence", Recipes.SOURCE_GEM, new ItemStack(ItemsRegistry.EARTH_ESSENCE.get()), 2000)
                .withPedestalItem(Ingredient.of(Tags.Items.INGOTS_IRON))
                .withPedestalItem(Ingredient.of(Tags.Items.SEEDS))
                .withPedestalItem(Ingredient.of(ItemTags.DIRT)));
        recipes.add(new ImbuementRecipe("water_essence", Recipes.SOURCE_GEM, new ItemStack(ItemsRegistry.WATER_ESSENCE.get()), 2000)
                .withPedestalItem(Items.WATER_BUCKET)
                .withPedestalItem(Items.SNOW_BLOCK)
                .withPedestalItem(Items.KELP));
        recipes.add(new ImbuementRecipe("conjuration_essence", Recipes.SOURCE_GEM, new ItemStack(ItemsRegistry.CONJURATION_ESSENCE.get()), 2000)
                .withPedestalItem(ItemsRegistry.WILDEN_HORN)
                .withPedestalItem(ItemsRegistry.STARBUNCLE_SHARD)
                .withPedestalItem(Items.BOOK));

        recipes.add(new ImbuementRecipe("abjuration_essence", Recipes.SOURCE_GEM, new ItemStack(ItemsRegistry.ABJURATION_ESSENCE.get()), 2000)
                .withPedestalItem(Items.FERMENTED_SPIDER_EYE)
                .withPedestalItem(Items.SUGAR)
                .withPedestalItem(Items.MILK_BUCKET));

        recipes.add(new ImbuementRecipe("manipulation_essence", Recipes.SOURCE_GEM, new ItemStack(ItemsRegistry.MANIPULATION_ESSENCE.get()), 2000)
                .withPedestalItem(Items.STONE_BUTTON)
                .withPedestalItem(Items.REDSTONE)
                .withPedestalItem(Items.CLOCK));

        Path output = this.generator.getOutputFolder();
        for (ImbuementRecipe g : recipes) {
            Path path = getRecipePath(output, g.getId().getPath());
            DataProvider.saveStable(cache, g.asRecipe(), path);
        }
    }

    private static Path getRecipePath(Path pathIn, String str) {
        return pathIn.resolve("data/ars_nouveau/recipes/" + str + ".json");
    }

    @Override
    public String getName() {
        return "Imbuement";
    }
}
