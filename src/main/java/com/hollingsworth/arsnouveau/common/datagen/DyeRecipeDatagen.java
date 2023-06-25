package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.JsonElement;
import com.hollingsworth.arsnouveau.common.crafting.recipes.DyeRecipe;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.ItemLike;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.api.RegistryHelper.getRegistryName;

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
        addDyeRecipe(ItemsRegistry.NOVICE_BOOTS);
        addDyeRecipe(ItemsRegistry.NOVICE_LEGGINGS);
        addDyeRecipe(ItemsRegistry.NOVICE_ROBES);
        addDyeRecipe(ItemsRegistry.NOVICE_HOOD);
        addDyeRecipe(ItemsRegistry.APPRENTICE_BOOTS);
        addDyeRecipe(ItemsRegistry.APPRENTICE_LEGGINGS);
        addDyeRecipe(ItemsRegistry.APPRENTICE_ROBES);
        addDyeRecipe(ItemsRegistry.APPRENTICE_HOOD);
        addDyeRecipe(ItemsRegistry.ARCHMAGE_BOOTS);
        addDyeRecipe(ItemsRegistry.ARCHMAGE_LEGGINGS);
        addDyeRecipe(ItemsRegistry.ARCHMAGE_ROBES);
        addDyeRecipe(ItemsRegistry.ARCHMAGE_HOOD);
        for (FileObj fileObj : files) {
            saveStable(pOutput, fileObj.element, fileObj.path);
        }
    }


    public void add(FileObj fileObj){
        files.add(fileObj);
    }

    public void addDyeRecipe(ItemLike inputItem){
        JsonElement dyeRecipe = DyeRecipe.asRecipe(inputItem.asItem());
        add(new FileObj(output.resolve("data/ars_nouveau/recipes/dye_" + getRegistryName(inputItem.asItem()).getPath() + ".json"), dyeRecipe));
    }

    @Override
    public String getName() {
        return "ArsNouveau: Json Datagen";
    }
    public record FileObj(Path path, JsonElement element){

    }
}
