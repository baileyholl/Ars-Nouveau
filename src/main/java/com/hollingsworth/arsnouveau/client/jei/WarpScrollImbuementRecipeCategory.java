package com.hollingsworth.arsnouveau.client.jei;

import com.hollingsworth.arsnouveau.common.crafting.recipes.WarpScrollImbuementRecipe;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Collections;

public class WarpScrollImbuementRecipeCategory extends MultiInputCategory<WarpScrollImbuementRecipe> {
    public IDrawable background;
    public IDrawable icon;

    public WarpScrollImbuementRecipeCategory(IGuiHelper helper) {
        super(helper, (imbuementRecipe -> new MultiProvider(imbuementRecipe.getOutput(), Collections.singletonList(Ingredient.of(imbuementRecipe.getCopyFrom())), imbuementRecipe.getInput())));
        background = helper.createBlankDrawable(114, 108);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockRegistry.IMBUEMENT_BLOCK));
    }

    @Override
    public RecipeType<WarpScrollImbuementRecipe> getRecipeType() {
        return JEIArsNouveauPlugin.WARP_SCROLL_IMBUEMENT_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("emi.category.ars_nouveau.warp_scroll_imbuement");
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
    public void draw(WarpScrollImbuementRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font renderer = Minecraft.getInstance().font;
        guiGraphics.drawString(renderer,  Component.translatable("ars_nouveau.source", recipe.getSource()), 0, 100, 10,false);
    }
}
