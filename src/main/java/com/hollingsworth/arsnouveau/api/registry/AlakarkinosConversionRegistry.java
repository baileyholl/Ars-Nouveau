package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.common.crafting.recipes.AlakarkinosRecipe;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.*;

public class AlakarkinosConversionRegistry {

    private static List<AlakarkinosRecipe> RECIPES = new ArrayList<>();
    private static Set<Block> CONVERTABLE_BLOCKS = Set.of();
    private static Map<Block, WeightedRandomList<WeightedEntry.Wrapper<AlakarkinosRecipe>>> CONVERTABLE_BLOCKS_MAP = new HashMap<>();

    public static List<AlakarkinosRecipe> getRecipes(){
        return Collections.unmodifiableList(RECIPES);
    }

    public static void reloadAlakarkinosRecipes(RecipeManager recipeManager){
        RECIPES = new ArrayList<>();
        List<AlakarkinosRecipe> recipes = recipeManager.getAllRecipesFor(RecipeRegistry.ALAKARKINOS_RECIPE_TYPE.get()).stream().map(RecipeHolder::value).toList();
        RECIPES.addAll(recipes);
        CONVERTABLE_BLOCKS = new HashSet<>();
        for (AlakarkinosRecipe recipe : RECIPES) {
            CONVERTABLE_BLOCKS.add(recipe.input());
        }
        CONVERTABLE_BLOCKS_MAP = new HashMap<>();
        for (AlakarkinosRecipe recipe : RECIPES) {
            var list = CONVERTABLE_BLOCKS_MAP.getOrDefault(recipe.input(), WeightedRandomList.create());
            var modifiedList = new ArrayList<>(list.unwrap());
            modifiedList.add(WeightedEntry.wrap(recipe, recipe.weight()));
            CONVERTABLE_BLOCKS_MAP.put(recipe.input(), WeightedRandomList.create(modifiedList));
        }
    }

    public static boolean isConvertable(Block block) {
        return CONVERTABLE_BLOCKS.contains(block);
    }

    public static int getTotalWeight(Block block) {
        return CONVERTABLE_BLOCKS_MAP.get(block).totalWeight;
    }

    public static @Nullable AlakarkinosRecipe getConversionResult(Block block, RandomSource random) {
        if (!isConvertable(block)) {
            return null;
        }
        var list = CONVERTABLE_BLOCKS_MAP.get(block);
        var entry = list.getRandom(random);
        return entry.map(WeightedEntry.Wrapper::data).orElse(null);
    }
}
