package com.hollingsworth.arsnouveau.common.datagen;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.recipe.DispelEntityRecipe;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DispelEntityProvider extends SimpleDataProvider{

    public List<DispelEntityRecipe> recipes = new ArrayList<>();

    public DispelEntityProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addEntries();
        for (DispelEntityRecipe recipe : recipes) {
            Path path = getRecipePath(output, recipe.getId().getPath());
            saveStable(pOutput, recipe.asRecipe(), path);
        }
    }

    protected void addEntries() {
        recipes.add(new DispelEntityRecipe(new ResourceLocation(ArsNouveau.MODID, "blaze_powder"), EntityType.BLAZE, EntityType.BLAZE.getDefaultLootTable(), new LootItemCondition[]{
                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnFire(true).build())).build()
        }));
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
