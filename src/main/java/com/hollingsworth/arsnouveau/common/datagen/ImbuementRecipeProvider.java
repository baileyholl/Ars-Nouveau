package com.hollingsworth.arsnouveau.common.datagen;


import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ImbuementRecipeProvider extends SimpleDataProvider{

    public List<ImbuementRecipe> recipes = new ArrayList<>();

    public ImbuementRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        collectJsons(pOutput);
        List<CompletableFuture<?>> futures = new ArrayList<>();
        return ModDatagen.registries.thenCompose((registry) -> {
            for (ImbuementRecipe g : recipes) {
                Path path = getRecipePath(output, g.id.getPath());
                futures.add(DataProvider.saveStable(pOutput, registry, ImbuementRecipe.CODEC, g, path));
            }
            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        });
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        recipes.add(new ImbuementRecipe("lapis", Ingredient.of(Tags.Items.GEMS_LAPIS), ItemsRegistry.SOURCE_GEM.get().getDefaultInstance(), 500));
        recipes.add(new ImbuementRecipe("amethyst", Ingredient.of(Items.AMETHYST_SHARD), ItemsRegistry.SOURCE_GEM.get().getDefaultInstance(), 500));
        recipes.add(new ImbuementRecipe("amethyst_block", Ingredient.of(Items.AMETHYST_BLOCK), new ItemStack(BlockRegistry.SOURCE_GEM_BLOCK), 2000));
        recipes.add(new ImbuementRecipe("fire_essence", RecipeDatagen.SOURCE_GEM, new ItemStack(ItemsRegistry.FIRE_ESSENCE.get()), 2000)
                .withPedestalItem(Items.FLINT_AND_STEEL)
                .withPedestalItem(Items.TORCH).withPedestalItem(Items.GUNPOWDER));
        recipes.add(new ImbuementRecipe("air_essence", RecipeDatagen.SOURCE_GEM, new ItemStack(ItemsRegistry.AIR_ESSENCE.get()), 2000)
                .withPedestalItem(Items.FEATHER)
                .withPedestalItem(ItemsRegistry.WILDEN_WING)
                .withPedestalItem(Ingredient.of(ItemTags.ARROWS)));
        recipes.add(new ImbuementRecipe("earth_essence", RecipeDatagen.SOURCE_GEM, new ItemStack(ItemsRegistry.EARTH_ESSENCE.get()), 2000)
                .withPedestalItem(Ingredient.of(Tags.Items.INGOTS_IRON))
                .withPedestalItem(Ingredient.of(Tags.Items.SEEDS))
                .withPedestalItem(Ingredient.of(ItemTags.DIRT)));
        recipes.add(new ImbuementRecipe("water_essence", RecipeDatagen.SOURCE_GEM, new ItemStack(ItemsRegistry.WATER_ESSENCE.get()), 2000)
                .withPedestalItem(Items.WATER_BUCKET)
                .withPedestalItem(Items.SNOW_BLOCK)
                .withPedestalItem(Items.KELP));
        recipes.add(new ImbuementRecipe("conjuration_essence", RecipeDatagen.SOURCE_GEM, new ItemStack(ItemsRegistry.CONJURATION_ESSENCE.get()), 2000)
                .withPedestalItem(ItemsRegistry.WILDEN_HORN)
                .withPedestalItem(ItemsRegistry.STARBUNCLE_SHARD)
                .withPedestalItem(Items.BOOK));

        recipes.add(new ImbuementRecipe("abjuration_essence", RecipeDatagen.SOURCE_GEM, new ItemStack(ItemsRegistry.ABJURATION_ESSENCE.get()), 2000)
                .withPedestalItem(Items.FERMENTED_SPIDER_EYE)
                .withPedestalItem(Items.SUGAR)
                .withPedestalItem(Items.MILK_BUCKET));

        recipes.add(new ImbuementRecipe("manipulation_essence", RecipeDatagen.SOURCE_GEM, new ItemStack(ItemsRegistry.MANIPULATION_ESSENCE.get()), 2000)
                .withPedestalItem(Items.STONE_BUTTON)
                .withPedestalItem(Items.REDSTONE)
                .withPedestalItem(Items.CLOCK));

        recipes.add(new ImbuementRecipe("pierce_arrow", Ingredient.of(ItemTags.ARROWS), new ItemStack(ItemsRegistry.PIERCE_ARROW.get()), 100)
                .withPedestalItem(ItemsRegistry.SOURCE_GEM.get())
                .withPedestalItem(ItemsRegistry.AIR_ESSENCE.get())
                .withPedestalItem(ItemsRegistry.WILDEN_SPIKE.get()));

        recipes.add(new ImbuementRecipe("amplify_arrow", Ingredient.of(ItemTags.ARROWS), new ItemStack(ItemsRegistry.AMPLIFY_ARROW.get()), 100)
                .withPedestalItem(ItemsRegistry.SOURCE_GEM.get())
                .withPedestalItem(ItemsRegistry.AIR_ESSENCE.get())
                .withPedestalItem(Ingredient.of(Tags.Items.GEMS_DIAMOND)));

        recipes.add(new ImbuementRecipe("split_arrow", Ingredient.of(ItemTags.ARROWS), new ItemStack(ItemsRegistry.SPLIT_ARROW.get()), 100)
                .withPedestalItem(ItemsRegistry.SOURCE_GEM.get())
                .withPedestalItem(ItemsRegistry.AIR_ESSENCE.get())
                .withPedestalItem(ItemsRegistry.WILDEN_HORN.get()));
    }

    private static Path getRecipePath(Path pathIn, String str) {
        return pathIn.resolve("data/ars_nouveau/recipe/" + str + ".json");
    }

    @Override
    public String getName() {
        return "Imbuement";
    }
}
