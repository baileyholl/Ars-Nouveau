package com.hollingsworth.arsnouveau.common.datagen;


import com.hollingsworth.arsnouveau.common.crafting.recipes.PotionFlaskRecipe;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;

import java.nio.file.Path;

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
