package com.hollingsworth.arsnouveau.common.datagen;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.crafting.recipes.SummonRitualRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.SummonRitualRecipe.WeightedMobType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SummonRitualProvider extends SimpleDataProvider {

    public List<SummonRitualRecipeWrapper> recipes = new ArrayList<>();

    public SummonRitualProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addEntries();
        for (SummonRitualRecipeWrapper recipe : recipes) {
            Path path = getRecipePath(output, recipe.id().getPath());
            saveStable(pOutput, SummonRitualRecipe.CODEC.encodeStart(JsonOps.INSTANCE, recipe.recipe()).getOrThrow(), path);
        }
    }

    protected void addEntries() {
        addMobRecipe(ArsNouveau.prefix("bats"), Ingredient.of(Items.AMETHYST_SHARD), EntityType.BAT);
        addMobRecipe(ArsNouveau.prefix("flying"), Ingredient.of(Items.PHANTOM_MEMBRANE, Items.TOTEM_OF_UNDYING, Items.AMETHYST_SHARD), 5,
                Pair.of(EntityType.ALLAY, 1),
                Pair.of(EntityType.VEX, 5),
                Pair.of(EntityType.PHANTOM, 5)
        );
    }

    public void addMobRecipe(ResourceLocation id, Ingredient augment, EntityType<?>... entityTypes) {
        this.addMobRecipe(id, augment, 5, entityTypes);
    }

    public void addMobRecipe(ResourceLocation id, Ingredient augment, int count, EntityType<? extends Entity>... entityTypes) {
        List<WeightedMobType> mobs = Arrays.stream(entityTypes)
                .map(type -> new WeightedMobType(EntityType.getKey(type)))
                .toList();

        addMobRecipe(id, augment, count, mobs);
    }

    public void addMobRecipe(ResourceLocation id, Ingredient augment, int count, Pair<EntityType<?>, Integer>... entityTypes) {
        List<WeightedMobType> mobs = Arrays.stream(entityTypes)
                .map(type -> new WeightedMobType(EntityType.getKey(type.getFirst()), type.getSecond()))
                .toList();

        addMobRecipe(id, augment, count, mobs);
    }

    public void addMobRecipe(ResourceLocation id, Ingredient augment, int count, List<WeightedMobType> mobs) {
        recipes.add(
                new SummonRitualRecipeWrapper(id,
                        new SummonRitualRecipe(augment, SummonRitualRecipe.MobSource.MOB_LIST, count, WeightedRandomList.create(mobs))
                )
        );
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
