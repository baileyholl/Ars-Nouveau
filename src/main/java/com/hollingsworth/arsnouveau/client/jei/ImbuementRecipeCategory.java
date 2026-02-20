package com.hollingsworth.arsnouveau.client.jei;

import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
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
import net.minecraft.world.item.crafting.RecipeHolder;

public class ImbuementRecipeCategory extends MultiInputCategory<ImbuementRecipe> {


    public IDrawable background;
    public IDrawable icon;

    public ImbuementRecipeCategory(IGuiHelper helper) {
        super(helper, (imbuementRecipe -> new MultiProvider(imbuementRecipe.output, imbuementRecipe.pedestalItems, imbuementRecipe.input)));
        background = helper.createBlankDrawable(114, 108);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockRegistry.IMBUEMENT_BLOCK));
    }

    @Override
    public RecipeType<RecipeHolder<ImbuementRecipe>> getRecipeType() {
        return JEIArsNouveauPlugin.IMBUEMENT_RECIPE_TYPE.get();
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.ars_nouveau.imbuement_chamber");
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
    public void draw(RecipeHolder<ImbuementRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        ImbuementRecipe recipe = recipeHolder.value();
        Font renderer = Minecraft.getInstance().font;
        guiGraphics.drawString(renderer, Component.translatable("ars_nouveau.source", recipe.source), 0, 100, 10, false);
    }
}
