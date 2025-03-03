package com.hollingsworth.arsnouveau.client.emi;

import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

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
    public void addWidgets(WidgetHolder widgets) {
        this.reset();
        MultiProvider provider = multiProvider;
        List<Ingredient> inputs = provider.input();
        double angleBetweenEach = 360.0 / inputs.size();
        var centerIngredient = this.getCenter();
        if (centerIngredient != null) {
            widgets.addSlot(centerIngredient, (int) this.center.x, (int) this.center.y);
        }

        for (EmiIngredient input : provider.getEmiInputs()) {
            widgets.addSlot(input, (int) point.x, (int) point.y);
            point = rotatePointAbout(point, center, angleBetweenEach);
        }

        var outputs = this.getOutputs();
        if (!outputs.isEmpty()) {
            widgets.addSlot(outputs.getFirst(), 100, 3).recipeContext(this);
        }

        widgets.addText(Component.translatable("ars_nouveau.source", recipe.source), 0, this.getDisplayHeight() - 10, 10,false);
    }
}
