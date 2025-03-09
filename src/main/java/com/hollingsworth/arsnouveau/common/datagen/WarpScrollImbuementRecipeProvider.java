package com.hollingsworth.arsnouveau.common.datagen;


import com.hollingsworth.arsnouveau.common.crafting.recipes.WarpScrollImbuementRecipe;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WarpScrollImbuementRecipeProvider extends SimpleDataProvider{

    public List<WarpScrollImbuementRecipe> recipes = new ArrayList<>();

    public WarpScrollImbuementRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput pOutput) {
        collectJsons(pOutput);
        List<CompletableFuture<?>> futures = new ArrayList<>();
        return ModDatagen.registries.thenCompose((registry) -> {
            for (WarpScrollImbuementRecipe g : recipes) {
                Path path = getRecipePath(output, g.id.getPath());
                futures.add(DataProvider.saveStable(pOutput, registry, WarpScrollImbuementRecipe.CODEC, g, path));
            }
            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        });
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        recipes.add(new WarpScrollImbuementRecipe("warp_scroll_copy", Ingredient.of(ItemsRegistry.WARP_SCROLL.get()), ItemsRegistry.WARP_SCROLL.get().getDefaultInstance(), ItemsRegistry.WARP_SCROLL.get().getDefaultInstance(), 1000));
        recipes.add(new WarpScrollImbuementRecipe("warp_scroll_copy_from_stable", Ingredient.of(ItemsRegistry.STABLE_WARP_SCROLL.get()), ItemsRegistry.WARP_SCROLL.get().getDefaultInstance(), ItemsRegistry.WARP_SCROLL.get().getDefaultInstance(), 1000));
        recipes.add(new WarpScrollImbuementRecipe("stable_warp_scroll_copy", Ingredient.of(ItemsRegistry.WARP_SCROLL.get()), ItemsRegistry.STABLE_WARP_SCROLL.get().getDefaultInstance(), ItemsRegistry.STABLE_WARP_SCROLL.get().getDefaultInstance(), 1000));
        recipes.add(new WarpScrollImbuementRecipe("stable_warp_scroll_copy_from_stable", Ingredient.of(ItemsRegistry.STABLE_WARP_SCROLL.get()), ItemsRegistry.STABLE_WARP_SCROLL.get().getDefaultInstance(), ItemsRegistry.STABLE_WARP_SCROLL.get().getDefaultInstance(), 1000));
    }

    private static Path getRecipePath(Path pathIn, String str) {
        return pathIn.resolve("data/ars_nouveau/recipe/warp_scroll_imbuement/" + str + ".json");
    }

    @Override
    public @NotNull String getName() {
        return "Warp Scroll Imbuement";
    }
}
