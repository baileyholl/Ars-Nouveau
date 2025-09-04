package com.hollingsworth.arsnouveau.client.emi;

import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class EmiGlyphRecipe extends EmiMultiInputRecipe<GlyphRecipe> {
    public EmiGlyphRecipe(ResourceLocation id, GlyphRecipe recipe) {
        super(id, recipe, new EmiMultiInputRecipe.MultiProvider(recipe.output, recipe.inputs, null));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiArsNouveauPlugin.GLYPH_CATEGORY;
    }

    @Override
    public int getDisplayWidth() {
        return 120;
    }

    @Override
    public int getDisplayHeight() {
        return 108;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        super.addWidgets(widgets);
        widgets.addText(Component.translatable("ars_nouveau.exp", ScribesTile.getLevelsFromExp(recipe.exp)), 0, 100, 10, false);
    }
}
