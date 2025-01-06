package com.hollingsworth.arsnouveau.client.jei;

import com.hollingsworth.arsnouveau.common.crafting.recipes.CrushRecipe;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectCrush;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class CrushRecipeCategory implements IRecipeCategory<CrushRecipe> {

    public IDrawable background;
    public IDrawable icon;
    private final IDrawable cachedArrows;

    public CrushRecipeCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(getWidth(), getHeight());
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, EffectCrush.INSTANCE.glyphItem.getDefaultInstance());
        this.cachedArrows = helper.createAnimatedRecipeArrow(40);
    }

    @Override
    public RecipeType<CrushRecipe> getRecipeType() {
        return JEIArsNouveauPlugin.CRUSH_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("ars_nouveau.crush_recipe");
    }

    @Override
    public int getWidth() {
        return 120;
    }

    @Override
    public int getHeight() {
        return 56;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(CrushRecipe recipe, @NotNull IRecipeSlotsView slotsView, @NotNull GuiGraphics matrixStack, double mouseX, double mouseY) {
        cachedArrows.draw(matrixStack, 22, 6);
        Font renderer = Minecraft.getInstance().font;
        for (int i = 0; i < recipe.outputs().size(); i++) {
            CrushRecipe.CrushOutput output = recipe.outputs().get(i);
            matrixStack.drawString(renderer, Math.round(100 * output.chance() - 0.5f) + "%", 98, 11 + 17 * i, 10,false);
            if(output.maxRange() > 1) {
                matrixStack.drawString(renderer, "1-" + output.maxRange(), 75, 11 + 17 * i, 10,false);
            }
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CrushRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 5).addIngredients(recipe.input());
        for (int i = 0; i < recipe.outputs().size(); i++) {
            CrushRecipe.CrushOutput output = recipe.outputs().get(i);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 50, 5 + 16 * i).addItemStack(output.stack());
        }
    }
}
