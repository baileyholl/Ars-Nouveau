package com.hollingsworth.arsnouveau.common.datagen;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.crafting.recipes.SummonRitualRecipe;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SummonRitualProvider extends SimpleDataProvider{

    public List<SummonRitualRecipe> recipes = new ArrayList<>();

    public SummonRitualProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addEntries();
        for (SummonRitualRecipe recipe : recipes) {
            Path path = getRecipePath(output, recipe.getId().getPath());
            saveStable(pOutput, recipe.asRecipe(), path);
        }
    }

    protected void addEntries() {
         ArrayList<SummonRitualRecipe.WeightedMobType> bats = new ArrayList<>();
         bats.add(new SummonRitualRecipe.WeightedMobType(EntityType.getKey(EntityType.BAT)));
         recipes.add(new SummonRitualRecipe(ArsNouveau.prefix( "bats"), Ingredient.of(Items.AMETHYST_SHARD), SummonRitualRecipe.MobSource.MOB_LIST, 5, bats));
    }

    protected static Path getRecipePath(Path path, String id) {
        return path.resolve("data/ars_nouveau/recipes/summon_ritual/" + id + ".json");
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return "Summon Ritual Datagen";
    }
}
