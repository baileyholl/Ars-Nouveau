package com.hollingsworth.arsnouveau.client.emi;

import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.List;

public class EmiEnchantingApparatusRecipe<T extends EnchantingApparatusRecipe> extends EmiMultiInputRecipe<T> {
    EmiIngredient fakeReagent = null;
    EmiStack fakeOutput = null;

    public EmiEnchantingApparatusRecipe(ResourceLocation id, T recipe) {
        super(id, recipe, new EmiMultiInputRecipe.MultiProvider(recipe.result(), recipe.pedestalItems(), recipe.reagent()));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiArsNouveauPlugin.ENCHANTING_APPARATUS_CATEGORY;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        ArrayList<EmiIngredient> inputs = new ArrayList<>(1 + this.recipe.pedestalItems().size());
        inputs.add(fakeReagent != null ? fakeReagent : EmiIngredient.of(this.recipe.reagent()));
        for (Ingredient ingredient : this.recipe.pedestalItems()) {
            inputs.add(EmiIngredient.of(ingredient));
        }

        return inputs;
    }

    @Override
    public EmiIngredient getCenter() {
        return this.fakeReagent != null ? fakeReagent : super.getCenter();
    }

    public EmiEnchantingApparatusRecipe<T> withReagentAndOutput(EmiIngredient reagent, EmiStack output) {
        var copy = new EmiEnchantingApparatusRecipe<>(this.id, this.recipe);
        copy.fakeReagent = reagent;
        copy.fakeOutput = output;
        return copy;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(this.fakeOutput != null ? this.fakeOutput : EmiStack.of(this.recipe.result()));
    }

    public void reset() {
        var w = this.getDisplayWidth();
        var h = this.getDisplayHeight();

        this.center = new Vec2((int) (w * 0.5) - 8, (int) (h * 0.5) - (this.recipe.consumesSource() ? 12 : 9));
        this.point = center.add(new Vec2(0, -32));
    }

    @Override
    public int getDisplayHeight() {
        var needsSource = this.recipe.consumesSource();
        return needsSource ? 100 : 86;
    }

    public void addSourceWidget(WidgetHolder widgets) {
        if (this.getRecipe().consumesSource()) {
            widgets.addText(Component.translatable("ars_nouveau.source", this.getRecipe().sourceCost()), 0, this.getDisplayHeight() - 10, 10, false);
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        super.addWidgets(widgets);
        this.addSourceWidget(widgets);
    }
}
