package com.hollingsworth.arsnouveau.client.jei;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CrushRecipe;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectCrush;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;

public class CrushRecipeCategory implements IRecipeCategory<CrushRecipe> {

    public IDrawable background;
    public IDrawable icon;
    private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;

    public CrushRecipeCategory(IGuiHelper helper){
        background = helper.createBlankDrawable(120,8 + 16 * 3);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ArsNouveauAPI.getInstance().getGlyphItem(EffectCrush.INSTANCE).getDefaultInstance());
        this.cachedArrows = CacheBuilder.newBuilder()
                .maximumSize(25)
                .build(new CacheLoader<>() {
                    @Override
                    public IDrawableAnimated load(Integer cookTime) {
                        return helper.drawableBuilder(JEIConstants.RECIPE_GUI_VANILLA, 82, 128, 24, 17)
                                .buildAnimated(cookTime, IDrawableAnimated.StartDirection.LEFT, false);
                    }
                });
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
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(CrushRecipe recipe, @Nonnull IRecipeSlotsView slotsView, @Nonnull PoseStack matrixStack, double mouseX, double mouseY) {
        IDrawableAnimated arrow = this.cachedArrows.getUnchecked(40);
        arrow.draw( matrixStack,30, 6);
        Font renderer = Minecraft.getInstance().font;
        for(int i = 0; i < recipe.outputs.size(); i++){
            renderer.draw(matrixStack, Math.round(100 * recipe.outputs.get(i).chance - 0.5f) + "%", 85f,11f + 17f * i, 10);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CrushRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 6,5).addIngredients(recipe.input);
        for(int i = 0; i < recipe.outputs.size(); i++){
            CrushRecipe.CrushOutput output = recipe.outputs.get(i);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 60, 5 + 16 * i ).addItemStack(output.stack);
        }
    }
}
