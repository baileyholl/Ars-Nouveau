package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.loot.DungeonLootTables;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CasterTomeData;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CasterTomeRegistry {

    private static List<RecipeHolder<CasterTomeData>> TOME_DATA = new ArrayList<>();

    public static List<RecipeHolder<CasterTomeData>> getTomeData() {
        return Collections.unmodifiableList(TOME_DATA);
    }

    public static List<RecipeHolder<CasterTomeData>> reloadTomeData(RecipeManager recipeManager, RegistryAccess access) {
        var recipes = recipeManager.recipeMap().byType(RecipeRegistry.CASTER_TOME_TYPE.get());
        DungeonLootTables.CASTER_TOMES = new ArrayList<>();
        TOME_DATA = new ArrayList<>();
        TOME_DATA.addAll(recipes);
        // 1.21.11: Recipe.getResultItem(RegistryAccess) removed; build the tome ItemStack via makeTome
        recipes.forEach(tome -> DungeonLootTables.CASTER_TOMES.add(() -> {
            CasterTomeData data = tome.value();
            net.minecraft.world.item.Item tomeItem = net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(data.tomeType());
            if (tomeItem == null || tomeItem == net.minecraft.world.item.Items.AIR) return net.minecraft.world.item.ItemStack.EMPTY;
            return CasterTomeData.makeTome(tomeItem, data.spell(), data.flavorText());
        }));
        return TOME_DATA;
    }

}
