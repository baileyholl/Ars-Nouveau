package com.hollingsworth.arsnouveau.common.datagen;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.crafting.recipes.DispelEntityRecipe;
import com.mojang.serialization.JsonOps;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DispelEntityProvider extends SimpleDataProvider{

    public List<Wrapper> recipes = new ArrayList<>();

    public DispelEntityProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addEntries();
        for (Wrapper recipe : recipes) {
            Path path = getRecipePath(output, recipe.location().getPath());
            saveStable(pOutput, DispelEntityRecipe.Serializer.CODEC.codec().encodeStart(JsonOps.INSTANCE, recipe.recipe).getOrThrow(), path);
        }
    }

    protected void addEntries() {
        recipes.add(new Wrapper(ArsNouveau.prefix( "blaze_powder"), new DispelEntityRecipe(EntityType.BLAZE, EntityType.BLAZE.getDefaultLootTable().location(), new LootItemCondition[]{
                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnFire(true))).build()
        })));
    }

    public record Wrapper(ResourceLocation location, DispelEntityRecipe recipe){

    }

    protected static Path getRecipePath(Path path, String id) {
        return path.resolve("data/ars_nouveau/recipes/dispel_entity/" + id + ".json");
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return "Dispel Entity Datagen";
    }
}
