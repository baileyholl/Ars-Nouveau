package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.JsonElement;
import com.hollingsworth.arsnouveau.common.crafting.recipes.DyeRecipe;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.world.level.ItemLike;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.api.RegistryHelper.getRegistryName;

public class JsonDatagen implements DataProvider {
    List<FileObj> files = new ArrayList<>();
    protected final DataGenerator generator;

    public JsonDatagen(DataGenerator generatorIn) {
        generator = generatorIn;
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

    }

    @Override
    public void run(CachedOutput cache) throws IOException {
        for (FileObj fileObj : files) {
            DataProvider.saveStable(cache, fileObj.element, fileObj.path);
        }
    }

    public void add(FileObj fileObj){
        files.add(fileObj);
    }

    public void addDyeRecipe(ItemLike inputItem){
        JsonElement dyeRecipe = DyeRecipe.asRecipe(inputItem.asItem());
        add(new FileObj(generator.getOutputFolder().resolve("data/ars_nouveau/recipes/dye_" + getRegistryName(inputItem.asItem()).getPath() + ".json"), dyeRecipe));
    }

    @Override
    public String getName() {
        return "ArsNouveau: Json Datagen";
    }
    public record FileObj(Path path, JsonElement element){

    }
}
