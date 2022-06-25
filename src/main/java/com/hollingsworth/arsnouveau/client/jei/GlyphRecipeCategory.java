package com.hollingsworth.arsnouveau.client.jei;

import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class GlyphRecipeCategory extends MultiInputCategory<GlyphRecipe> {

    public IDrawable background;
    public IDrawable icon;

    public GlyphRecipeCategory(IGuiHelper helper){
        super(helper, (glyphRecipe -> new MultiProvider(glyphRecipe.output, glyphRecipe.inputs, null)));
        background = helper.createBlankDrawable(114,108);
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
    public void draw(GlyphRecipe recipe, @Nonnull IRecipeSlotsView slotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        Font renderer = Minecraft.getInstance().font;
        renderer.draw(matrixStack, Component.translatable("ars_nouveau.exp", ScribesTile.getLevelsFromExp(recipe.exp)), 0.0f,100f, 10);
    }
}
