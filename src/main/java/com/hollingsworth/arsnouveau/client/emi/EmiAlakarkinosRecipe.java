package com.hollingsworth.arsnouveau.client.emi;

import com.hollingsworth.arsnouveau.api.registry.AlakarkinosConversionRegistry;
import com.hollingsworth.arsnouveau.common.crafting.recipes.AlakarkinosRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EmiAlakarkinosRecipe  implements EmiRecipe {
    public static float ITEMS_PER_ROW = 7f;

    private final ResourceLocation id;
    private final AlakarkinosRecipe recipe;

    public EmiAlakarkinosRecipe(ResourceLocation id, AlakarkinosRecipe recipe) {
        this.id = id;
        this.recipe = recipe;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiArsNouveauPlugin.ALAKARKINOS_CATEGORY;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(EmiStack.of(this.recipe.input()));
    }

    @Override
    public List<EmiStack> getOutputs() {
        if (this.recipe.drops().isPresent()) {
            List<EmiStack> out = new ArrayList<>();
            for (var drop : this.recipe.drops().get()) {
                out.add(EmiStack.of(drop.item()));
            }
            return out;
        }

        return List.of();
    }

    @Override
    public int getDisplayWidth() {
        return 126;
    }

    @Override
    public int getDisplayHeight() {
        return ((int) Math.floor(this.getOutputs().size() / ITEMS_PER_ROW) + 1) * 18 + 9 + 22;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        String prepared = recipe.table().location().getPath().replace("archaeology/", "").replaceAll("_[0-9]", "").replaceAll("_", " ").toLowerCase(Locale.ROOT);
        String name = WordUtils.capitalizeFully(prepared);
        widgets.addText(Component.literal(name), 22, 4, 0xFF000000, false);

        DecimalFormat df = new DecimalFormat("##.##%");
        String recipeChance = df.format((float) recipe.weight() / AlakarkinosConversionRegistry.getTotalWeight(recipe.input()));
        widgets.addSlot(EmiStack.of(recipe.input()), 0, 0).appendTooltip(
                Component.translatable("ars_nouveau.alakarkinos_recipe.chance", recipeChance)
        );

        recipe.drops().ifPresent(drops -> {
            int yOffset = 9;
            int i = (int) ITEMS_PER_ROW;

            for (AlakarkinosRecipe.LootDrop drop : drops) {
                int row = (int) Math.floor(i / ITEMS_PER_ROW);
                int x = (int) ((i - (row * ITEMS_PER_ROW)) * 18);
                int y = row * 18 + yOffset;

                String chance = df.format(drop.chance());
                widgets.addSlot(EmiStack.of(drop.item()), x, y).appendTooltip(
                        Component.translatable("ars_nouveau.alakarkinos_recipe.chance", chance)
                ).recipeContext(this);
                i += 1;
            }
        });
    }
}
