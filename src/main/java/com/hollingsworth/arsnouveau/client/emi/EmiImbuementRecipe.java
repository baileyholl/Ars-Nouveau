package com.hollingsworth.arsnouveau.client.emi;

import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class EmiImbuementRecipe extends EmiMultiInputRecipe<ImbuementRecipe> {
    public EmiImbuementRecipe(ResourceLocation id, ImbuementRecipe recipe) {
        super(id, recipe, new EmiMultiInputRecipe.MultiProvider(recipe.output, recipe.pedestalItems, recipe.input));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiArsNouveauPlugin.IMBUEMENT_CATEGORY;
    }

    @Override
    public int getDisplayWidth() {
        return 114;
    }

    @Override
    public int getDisplayHeight() {
        return recipe.pedestalItems.size() <= 3 ? 86 : 110;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        super.addWidgets(widgets);
        widgets.addText(Component.translatable("ars_nouveau.source", recipe.source), 0, 76, 10,false);
    }
}
