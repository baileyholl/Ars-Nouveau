package com.hollingsworth.arsnouveau.client.emi;

import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

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
        super.addWidgets(widgets);
        widgets.addText(Component.translatable("ars_nouveau.source", recipe.source), 0, this.getDisplayHeight() - 10, 10,false);
    }
}
