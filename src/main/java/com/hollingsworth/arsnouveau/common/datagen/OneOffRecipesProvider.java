package com.hollingsworth.arsnouveau.common.datagen;


import com.hollingsworth.arsnouveau.common.crafting.recipes.BookUpgradeRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.PotionFlaskRecipe;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.NonNullList;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;

import java.nio.file.Path;
import java.util.List;

public class OneOffRecipesProvider extends SimpleDataProvider{

    public OneOffRecipesProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        var flasks = new Item[]{
                ItemsRegistry.POTION_FLASK.get(),
                ItemsRegistry.POTION_FLASK_EXTEND_TIME.get(),
                ItemsRegistry.POTION_FLASK_AMPLIFY.get()
        };

        for(int i = 0; i < flasks.length; i++){
            var flaskRecipe = new PotionFlaskRecipe("", flasks[i].getDefaultInstance(), flasks[i].getDefaultInstance());
            saveStable(pOutput, PotionFlaskRecipe.CODEC.encodeStart(JsonOps.INSTANCE, flaskRecipe).getOrThrow(), getRecipePath(output, "fill_potion_flask_" + i));
        }

        BookUpgradeRecipe apprentice = new BookUpgradeRecipe("", CraftingBookCategory.MISC, ItemsRegistry.APPRENTICE_SPELLBOOK.get().getDefaultInstance(),
                NonNullList.copyOf(List.of(
                        Ingredient.of(ItemsRegistry.NOVICE_SPELLBOOK.get().getDefaultInstance()),
                        Ingredient.of(Tags.Items.OBSIDIANS),
                        Ingredient.of(Tags.Items.GEMS_DIAMOND),
                        Ingredient.of(Tags.Items.GEMS_DIAMOND),
                        Ingredient.of(Tags.Items.GEMS_DIAMOND),
                        Ingredient.of(Items.QUARTZ_BLOCK),
                        Ingredient.of(Items.QUARTZ_BLOCK),
                        Ingredient.of(Tags.Items.RODS_BLAZE),
                        Ingredient.of(Tags.Items.RODS_BLAZE)
                        )));

        BookUpgradeRecipe archmage = new BookUpgradeRecipe("", CraftingBookCategory.MISC, ItemsRegistry.ARCHMAGE_SPELLBOOK.get().getDefaultInstance(),
                NonNullList.copyOf(List.of(
                        Ingredient.of(ItemsRegistry.APPRENTICE_SPELLBOOK.get().getDefaultInstance()),
                        Ingredient.of(Tags.Items.ENDER_PEARLS),
                        Ingredient.of(Tags.Items.ENDER_PEARLS),
                        Ingredient.of(Tags.Items.ENDER_PEARLS),
                        Ingredient.of(Tags.Items.GEMS_EMERALD),
                        Ingredient.of(Tags.Items.GEMS_EMERALD),
                        Ingredient.of(Items.TOTEM_OF_UNDYING),
                        Ingredient.of(Items.NETHER_STAR),
                        Ingredient.of(ItemsRegistry.WILDEN_TRIBUTE)
                )));
        saveStable(pOutput, BookUpgradeRecipe.CODEC.encodeStart(JsonOps.INSTANCE, apprentice).getOrThrow(), getRecipePath(output, "apprentice_book_upgrade"));

        saveStable(pOutput, BookUpgradeRecipe.CODEC.encodeStart(JsonOps.INSTANCE, archmage).getOrThrow(), getRecipePath(output, "archmage_book_upgrade"));
    }



    protected static Path getRecipePath(Path path, String id) {
        return path.resolve("data/ars_nouveau/recipe/" + id + ".json");
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return "Grab bag Datagen";
    }

}
