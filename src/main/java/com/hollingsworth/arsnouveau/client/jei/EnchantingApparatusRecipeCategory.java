package com.hollingsworth.arsnouveau.client.jei;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.util.ResourceLocation;

public class EnchantingApparatusRecipeCategory implements IRecipeCategory {
    @Override
    public ResourceLocation getUid() {
        return null;
    }

    @Override
    public Class getRecipeClass() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public IDrawable getBackground() {
        return null;
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setIngredients(Object o, IIngredients iIngredients) {

    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, Object o, IIngredients iIngredients) {

    }
}
