package com.hollingsworth.arsnouveau.client.jei;

import com.hollingsworth.arsnouveau.api.recipe.BuddingConversionRecipe;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class BuddingConversionRecipeCategory implements IRecipeCategory<BuddingConversionRecipe> {
    private final IDrawableAnimated arrow;
    public IDrawable background;
    public IDrawable icon;

    public BuddingConversionRecipeCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(120, 24);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ItemsRegistry.AMETHYST_GOLEM_CHARM.asItem().getDefaultInstance());
        arrow = helper.drawableBuilder(JEIConstants.RECIPE_GUI_VANILLA, 82, 128, 24, 17)
                .buildAnimated(40, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public RecipeType<BuddingConversionRecipe> getRecipeType() {
        return JEIArsNouveauPlugin.BUDDING_CONVERSION_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("ars_nouveau.budding_conversion_recipe");
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
    public void draw(BuddingConversionRecipe recipe, @NotNull IRecipeSlotsView slotsView, @NotNull GuiGraphics matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack, 48, 5   );
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BuddingConversionRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 120-16-6, 4).addIngredient(VanillaTypes.ITEM_STACK, recipe.result().asItem().getDefaultInstance());
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 4).addIngredient(VanillaTypes.ITEM_STACK, recipe.input().asItem().getDefaultInstance());
    }
}
