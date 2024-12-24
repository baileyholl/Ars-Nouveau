package com.hollingsworth.arsnouveau.api.documentation.entry;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;

public class GlyphRecipeEntry extends PedestalRecipeEntry{
    RecipeHolder<GlyphRecipe> recipe;

    public GlyphRecipeEntry(RecipeHolder<GlyphRecipe> recipe, BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        this.recipe = recipe;
        this.image = DocAssets.SCRIBES_RECIPE;
        this.title = Component.translatable("block.ars_nouveau.scribes_table");
        spinning = true;
        drawPedestals = false;
        if(recipe != null && recipe.value() != null) {
            this.outputStack = recipe.value().output;
            this.ingredients = recipe.value().inputs;
        }
    }

    public static SinglePageCtor create(RecipeHolder<GlyphRecipe> recipe){
        return (parent, x, y, width, height) -> new GlyphRecipeEntry(recipe, parent, x, y, width, height);
    }
}
