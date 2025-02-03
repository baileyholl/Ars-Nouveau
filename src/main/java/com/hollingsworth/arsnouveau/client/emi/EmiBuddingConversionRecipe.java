package com.hollingsworth.arsnouveau.client.emi;

import com.hollingsworth.arsnouveau.common.crafting.recipes.BuddingConversionRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmiBuddingConversionRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final BuddingConversionRecipe recipe;

    public EmiBuddingConversionRecipe(ResourceLocation id, BuddingConversionRecipe recipe) {
        this.id = id;
        this.recipe = recipe;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiArsNouveauPlugin.BUDDING_CONVERSION_CATEGORY;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(EmiIngredient.of(Ingredient.of(this.recipe.input())));
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(EmiStack.of(this.recipe.result()));
    }

    @Override
    public int getDisplayWidth() {
        return 120;
    }

    @Override
    public int getDisplayHeight() {
        return 24;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(EmiStack.of(recipe.input()), 6, 4);
        widgets.addFillingArrow(48, 5, 40 * 50);
        widgets.addSlot(EmiStack.of(recipe.result()), 120 - 16 - 6, 4).recipeContext(this);
    }
}
