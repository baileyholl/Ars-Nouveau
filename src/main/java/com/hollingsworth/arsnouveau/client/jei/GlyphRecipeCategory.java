package com.hollingsworth.arsnouveau.client.jei;

import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
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

public class GlyphRecipeCategory extends MultiInputCategory<GlyphRecipe> {

    public IDrawable background;
    public IDrawable icon;

    public GlyphRecipeCategory(IGuiHelper helper) {
        super(helper, (glyphRecipe -> new MultiProvider(glyphRecipe.output, glyphRecipe.inputs, null)));
        background = helper.createBlankDrawable(114, 108);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockRegistry.SCRIBES_BLOCK));
    }

    @Override
    public RecipeType<GlyphRecipe> getRecipeType() {
        return JEIArsNouveauPlugin.GLYPH_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.ars_nouveau.scribes_table");
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
    public void draw(GlyphRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font renderer = Minecraft.getInstance().font;
        guiGraphics.drawString(renderer, Component.translatable("ars_nouveau.exp", ScribesTile.getLevelsFromExp(recipe.exp)), 0, 100, 10, false);
    }
}
