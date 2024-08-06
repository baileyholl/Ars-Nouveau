package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.loot.DungeonLootTables;
import com.hollingsworth.arsnouveau.common.tomes.CasterTomeData;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CasterTomeRegistry {

    private static List<CasterTomeData> TOME_DATA = new ArrayList<>();

    public static List<CasterTomeData> getTomeData(){
        return Collections.unmodifiableList(TOME_DATA);
    }

    public static List<CasterTomeData> reloadTomeData(RecipeManager recipeManager, Level level){
        var recipes = recipeManager.getAllRecipesFor(RecipeRegistry.CASTER_TOME_TYPE.get());
        DungeonLootTables.CASTER_TOMES = new ArrayList<>();
        TOME_DATA = new ArrayList<>();
        TOME_DATA.addAll(recipes);
        recipes.forEach(tome -> DungeonLootTables.CASTER_TOMES.add(() -> tome.getResultItem(level.registryAccess())));
        return TOME_DATA;
    }

}
