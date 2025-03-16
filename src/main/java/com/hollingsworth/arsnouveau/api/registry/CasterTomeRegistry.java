package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.loot.DungeonLootTables;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CasterTomeData;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

public class CasterTomeRegistry {

    @Deprecated
    public static List<RecipeHolder<CasterTomeData>> getTomeData(){
        return ANRegistries.CASTER_TOMES.entrySet().stream().map(e -> new RecipeHolder<>(e.getKey().location(), e.getValue())).toList();
    }

    public static List<RecipeHolder<CasterTomeData>> reloadTomeData(RecipeManager recipeManager, RegistryAccess access) {
        var recipes = recipeManager.getAllRecipesFor(RecipeRegistry.CASTER_TOME_TYPE.get());
        DungeonLootTables.CASTER_TOMES = new ArrayList<>();
        for (var recipe : recipes) {
            Registry.registerForHolder(ANRegistries.CASTER_TOMES, recipe.id(), recipe.value());
            DungeonLootTables.CASTER_TOMES.add(() -> recipe.value().getResultItem(access));
        }

        return getTomeData();
    }

}
