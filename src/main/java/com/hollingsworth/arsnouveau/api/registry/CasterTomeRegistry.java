package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.loot.DungeonLootTables;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CasterTomeData;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

public class CasterTomeRegistry {

    public static final Registry<CasterTomeData> CASTER_TOMES = new MappedRegistry<>(ResourceKey.createRegistryKey(ArsNouveau.prefix("caster_tomes")), Lifecycle.stable());

    @Deprecated
    public static List<RecipeHolder<CasterTomeData>> getTomeData() {
        return CASTER_TOMES.entrySet().stream().map(e -> new RecipeHolder<>(e.getKey().location(), e.getValue())).toList();
    }

    public static List<RecipeHolder<CasterTomeData>> reloadTomeData(RecipeManager recipeManager, RegistryAccess access) {
        var recipes = recipeManager.getAllRecipesFor(RecipeRegistry.CASTER_TOME_TYPE.get());
        DungeonLootTables.CASTER_TOMES = new ArrayList<>();
        for (var recipe : recipes) {
            Registry.registerForHolder(CASTER_TOMES, recipe.id(), recipe.value());
            DungeonLootTables.CASTER_TOMES.add(() -> recipe.value().getResultItem(access));
        }

        return getTomeData();
    }

}
