package com.hollingsworth.arsnouveau.client.emi;

import com.hollingsworth.arsnouveau.common.crafting.recipes.CrushRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmiCrushRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final CrushRecipe recipe;

    public EmiCrushRecipe(ResourceLocation id, CrushRecipe recipe) {
        this.id = id;
        this.recipe = recipe;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiArsNouveauPlugin.CRUSH_CATEGORY;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(EmiIngredient.of(this.recipe.input()));
    }

    @Override
    public List<EmiStack> getOutputs() {
        return this.recipe.outputs().stream().map(CrushRecipe.CrushOutput::stack).map(EmiStack::of).toList();
    }

    @Override
    public int getDisplayWidth() {
        return 120;
    }

    @Override
    public int getDisplayHeight() {
        return 86;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(EmiIngredient.of(recipe.input()), 4, this.getDisplayHeight() / 2 - 10);
        widgets.addFillingArrow(24, this.getDisplayHeight() / 2 - 10, 40 * 50);

        for (int i = recipe.outputs().size() - 1; i >= 0; i--) {
            CrushRecipe.CrushOutput output = recipe.outputs().get(i);
            widgets.addSlot(EmiStack.of(output.stack()).setChance(output.chance()), 53, 5 + 16 * i).recipeContext(this);
            widgets.addText(Component.literal(Math.round(100 * output.chance() - 0.5f) + "%"), 97, 11 + 17 * i, 10,false);
            if(output.maxRange() > 1) {
                widgets.addText(Component.literal("1-" + output.maxRange()), 75, 11 + 17 * i, 10,false);
            }
        }
    }
}
