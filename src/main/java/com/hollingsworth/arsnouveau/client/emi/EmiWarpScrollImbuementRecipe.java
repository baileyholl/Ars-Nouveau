package com.hollingsworth.arsnouveau.client.emi;

import com.hollingsworth.arsnouveau.common.crafting.recipes.WarpScrollImbuementRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Collections;

public class EmiWarpScrollImbuementRecipe extends EmiMultiInputRecipe<WarpScrollImbuementRecipe> {
    public EmiWarpScrollImbuementRecipe(ResourceLocation id, WarpScrollImbuementRecipe recipe) {
        super(id, recipe, new MultiProvider(recipe.getOutput(), Collections.singletonList(Ingredient.of(recipe.getCopyFrom())), recipe.getInput()));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiArsNouveauPlugin.WARP_SCROLL_IMBUEMENT_CATEGORY;
    }

    @Override
    public int getDisplayHeight() {
        return 86;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        super.addWidgets(widgets);
        widgets.addText(Component.translatable("ars_nouveau.source", recipe.getSource()), 0, this.getDisplayHeight() - 10, 10,false);
    }
}
