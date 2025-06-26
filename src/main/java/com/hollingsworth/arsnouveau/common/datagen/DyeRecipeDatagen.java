package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.JsonElement;
import com.hollingsworth.arsnouveau.common.crafting.recipes.DyeRecipe;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.NonNullList;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class DyeRecipeDatagen extends SimpleDataProvider {
    List<FileObj> files = new ArrayList<>();

    public DyeRecipeDatagen(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addDyeRecipe(ItemsRegistry.APPRENTICE_SPELLBOOK.asItem());
        addDyeRecipe(ItemsRegistry.NOVICE_SPELLBOOK.asItem());
        addDyeRecipe(ItemsRegistry.ARCHMAGE_SPELLBOOK.asItem());
        addDyeRecipe(ItemsRegistry.SORCERER_BOOTS);
        addDyeRecipe(ItemsRegistry.SORCERER_LEGGINGS);
        addDyeRecipe(ItemsRegistry.SORCERER_ROBES);
        addDyeRecipe(ItemsRegistry.SORCERER_HOOD);
        addDyeRecipe(ItemsRegistry.ARCANIST_BOOTS);
        addDyeRecipe(ItemsRegistry.ARCANIST_LEGGINGS);
        addDyeRecipe(ItemsRegistry.ARCANIST_ROBES);
        addDyeRecipe(ItemsRegistry.ARCANIST_HOOD);
        addDyeRecipe(ItemsRegistry.BATTLEMAGE_BOOTS);
        addDyeRecipe(ItemsRegistry.BATTLEMAGE_LEGGINGS);
        addDyeRecipe(ItemsRegistry.BATTLEMAGE_ROBES);
        addDyeRecipe(ItemsRegistry.BATTLEMAGE_HOOD);
        addDyeRecipe(ItemsRegistry.CREATIVE_SPELLBOOK);
        addDyeRecipe(ItemsRegistry.ENCHANTERS_SWORD);
        addDyeRecipe(ItemsRegistry.WAND);
        addDyeRecipe(ItemsRegistry.SPELL_BOW);
        addDyeRecipe(ItemsRegistry.SCRY_CASTER);
        addDyeRecipe(ItemsRegistry.SPELL_CROSSBOW);
        addDyeRecipe(ItemsRegistry.SPELL_PARCHMENT);

        for (FileObj fileObj : files) {
            saveStable(pOutput, fileObj.element, fileObj.path);
        }
    }


    public void add(FileObj fileObj) {
        files.add(fileObj);
    }

    public void addDyeRecipe(ItemLike inputItem) {
        var dyeRecipe = new DyeRecipe("", CraftingBookCategory.MISC, inputItem.asItem().getDefaultInstance(), NonNullList.of(Ingredient.EMPTY, Ingredient.of(Tags.Items.DYES), Ingredient.of(inputItem)));
        files.add(new FileObj(resolvePath("data/ars_nouveau/recipe/dye_" + getRegistryName(inputItem.asItem()).getPath() + ".json"), DyeRecipe.CODEC.encodeStart(JsonOps.INSTANCE, dyeRecipe).getOrThrow()));
    }

    @Override
    public @NotNull String getName() {
        return "ArsNouveau: Json Datagen";
    }

    Path resolvePath(String path) {
        return this.generator.getPackOutput().getOutputFolder().resolve(path);
    }

    public record FileObj(Path path, JsonElement element) {

    }
}
