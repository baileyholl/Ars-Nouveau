package com.hollingsworth.arsnouveau.client.emi;

import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class EmiEnchantingApparatusRecipe<T extends EnchantingApparatusRecipe> extends EmiMultiInputRecipe<T> {
    public EmiEnchantingApparatusRecipe(ResourceLocation id, T recipe) {
        super(id, recipe, new EmiMultiInputRecipe.MultiProvider(recipe.result(), recipe.pedestalItems(), recipe.reagent()));
        this.centerOffset = recipe.consumesSource() ? 12 : 9;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiArsNouveauPlugin.ENCHANTING_APPARATUS_CATEGORY;
    }

    @Override
    protected List<EmiIngredient> generateInputs() {
        List<EmiIngredient> inputs = new ArrayList<>(1 + this.recipe.pedestalItems().size());
        inputs.add(EmiIngredient.of(this.recipe.reagent()));
        for (Ingredient ingredient : this.recipe.pedestalItems()) {
            inputs.add(EmiIngredient.of(ingredient));
        }

        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(EmiStack.of(this.recipe.result()));
    }

    @Override
    public int getDisplayHeight() {
        var needsSource = this.recipe.consumesSource();
        return needsSource ? 100 : 86;
    }

    public void addSourceWidget(WidgetHolder widgets) {
        if (this.getRecipe().consumesSource()) {
            widgets.addText(Component.translatable("ars_nouveau.source", this.getRecipe().sourceCost()), 0, widgets.getHeight() - 10, 10, false);
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        super.addWidgets(widgets);
        this.addSourceWidget(widgets);
    }
}
