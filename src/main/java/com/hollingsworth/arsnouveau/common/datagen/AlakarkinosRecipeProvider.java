package com.hollingsworth.arsnouveau.common.datagen;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.crafting.recipes.AlakarkinosRecipe;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AlakarkinosRecipeProvider extends SimpleDataProvider {

    public List<Wrapper> recipes = new ArrayList<>();

    public AlakarkinosRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addEntries();
        for (Wrapper recipe : recipes) {
            Path path = getRecipePath(output, recipe.location().getPath());
            saveStable(pOutput, ANCodecs.toJson(AlakarkinosRecipe.CODEC, recipe.recipe), path);
        }
    }

    protected void addEntries() {
        recipes.add(new Wrapper(ArsNouveau.prefix("desert_well"), new AlakarkinosRecipe(Blocks.SAND, BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY, 100)));
        recipes.add(new Wrapper(ArsNouveau.prefix("desert_pyramid"), new AlakarkinosRecipe(Blocks.SAND, BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY, 25)));

        recipes.add(new Wrapper(ArsNouveau.prefix("ocean_ruins_warm"), new AlakarkinosRecipe(Blocks.SAND, BuiltInLootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY, 25)));
        recipes.add(new Wrapper(ArsNouveau.prefix("ocean_ruins_cold"), new AlakarkinosRecipe(Blocks.GRAVEL, BuiltInLootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY, 25)));

        recipes.add(new Wrapper(ArsNouveau.prefix("trail_ruins_common"), new AlakarkinosRecipe(Blocks.GRAVEL, BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_COMMON, 100)));
        recipes.add(new Wrapper(ArsNouveau.prefix("trail_ruins_rare"), new AlakarkinosRecipe(Blocks.GRAVEL, BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE, 25)));
    }

    public record Wrapper(ResourceLocation location, AlakarkinosRecipe recipe) {

    }

    protected static Path getRecipePath(Path path, String id) {
        return path.resolve("data/ars_nouveau/recipe/alakarkinos/" + id + ".json");
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return "Alakarkinos Recipe Datagen";
    }
}
