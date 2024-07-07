package com.hollingsworth.arsnouveau.common.datagen;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.crafting.recipes.SummonRitualRecipe;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SummonRitualProvider extends SimpleDataProvider{

    public List<SummonRitualRecipeWrapper> recipes = new ArrayList<>();

    public SummonRitualProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addEntries();
        for (SummonRitualRecipeWrapper recipe : recipes) {
            Path path = getRecipePath(output, recipe.id().getPath());
            saveStable(pOutput, SummonRitualRecipe.Serializer.CODEC.codec().encodeStart(JsonOps.INSTANCE, recipe.recipe()).getOrThrow(), path);
        }
    }

    protected void addEntries() {
         ArrayList<SummonRitualRecipe.WeightedMobType> bats = new ArrayList<>();
         bats.add(new SummonRitualRecipe.WeightedMobType(EntityType.getKey(EntityType.BAT)));
         recipes.add(new SummonRitualRecipeWrapper(ArsNouveau.prefix( "bats"), new SummonRitualRecipe(Ingredient.of(Items.AMETHYST_SHARD), SummonRitualRecipe.MobSource.MOB_LIST, 5, WeightedRandomList.create(bats))));
    }

    protected static Path getRecipePath(Path path, String id) {
        return path.resolve("data/ars_nouveau/recipe/summon_ritual/" + id + ".json");
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return "Summon Ritual Datagen";
    }

    public static class SummonRitualRecipeWrapper {
        private final SummonRitualRecipe recipe;
        private final ResourceLocation id;

        public SummonRitualRecipeWrapper(ResourceLocation id, SummonRitualRecipe recipe) {
            this.recipe = recipe;
            this.id = id;
        }

        public SummonRitualRecipe recipe() {
            return recipe;
        }

        public ResourceLocation id() {
            return id;
        }
    }
}
