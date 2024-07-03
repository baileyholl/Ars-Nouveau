package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.JsonElement;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.ItemLike;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
        for (FileObj fileObj : files) {
            saveStable(pOutput, fileObj.element, fileObj.path);
        }
    }


    public void add(FileObj fileObj){
        files.add(fileObj);
    }

    public void addDyeRecipe(ItemLike inputItem){
        //todo: restore dye serializer
//        var dyeRecipe = new DyeRecipe("", CraftingBookCategory.MISC, inputItem, List.of())
//        RecipeRegistry.DYE_RECIPE.get().codec().codec().encode(new DyeRecipe())
//        add(new FileObj(output.resolve("data/ars_nouveau/recipes/dye_" + getRegistryName(inputItem.asItem()).getPath() + ".json"), DyeRecipe.));
    }

    @Override
    public String getName() {
        return "ArsNouveau: Json Datagen";
    }
    public record FileObj(Path path, JsonElement element){

    }
}
