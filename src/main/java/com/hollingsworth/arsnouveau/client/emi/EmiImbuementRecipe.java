package com.hollingsworth.arsnouveau.client.emi;

import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class EmiImbuementRecipe extends EmiMultiInputRecipe<ImbuementRecipe> {
    public EmiImbuementRecipe(ResourceLocation id, ImbuementRecipe recipe) {
        super(id, recipe, new MultiProvider(recipe.output, recipe.pedestalItems, recipe.input));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiArsNouveauPlugin.IMBUEMENT_CATEGORY;
    }

    @Override
    public int getDisplayHeight() {
        return switch (this.recipe.pedestalItems.size()) {
            case 0, 1, 3 -> 86;
            default -> 100;
        };
    }

    @Override
    protected List<EmiIngredient> generateInputs() {
        var inputs = super.generateInputs();
        for (int i = 1; i < inputs.size(); i++) {
            var ingredient = inputs.get(i);
            if (ingredient.getEmiStacks().size() == 1 && ingredient.getEmiStacks().get(0) instanceof EmiStack stack) {
                stack.setRemainder(stack);
            } else {
                // Hack to get EMI to recognise that the Ingredient is not used
                ingredient.setChance(0);
            }
        }

        return inputs;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        super.addWidgets(widgets);
        widgets.addText(Component.translatable("ars_nouveau.source", recipe.source), 0, this.getDisplayHeight() - 10, 10, false);
    }
}
