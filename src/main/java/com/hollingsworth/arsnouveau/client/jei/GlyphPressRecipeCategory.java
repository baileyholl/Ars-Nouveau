package com.hollingsworth.arsnouveau.client.jei;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.recipe.GlyphPressRecipe;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GlyphPressRecipeCategory implements IRecipeCategory<GlyphPressRecipe> {

    public IDrawable background;
    public IDrawable icon;
    IGuiHelper helper;
    public final static ResourceLocation UID = new ResourceLocation(ArsNouveau.MODID, "glyph_recipe");
    private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;

    public GlyphPressRecipeCategory(IGuiHelper helper){
        this.helper = helper;
        background = helper.createBlankDrawable(60,30);
        icon = helper.createDrawableIngredient(new ItemStack(BlockRegistry.GLYPH_PRESS_BLOCK));
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
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends GlyphPressRecipe> getRecipeClass() {
        return GlyphPressRecipe.class;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("block.ars_nouveau.glyph_press");
    }

    @Override
    public IDrawable getBackground() {
        return helper.createBlankDrawable(80,30);
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(GlyphPressRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
        IDrawableAnimated arrow = this.cachedArrows.getUnchecked(40);
        arrow.draw( matrixStack,38, 6);
    }


    @Override
    public void setIngredients(GlyphPressRecipe glyphPressRecipe, IIngredients iIngredients) {
        ItemStack clay = glyphPressRecipe.tier == SpellTier.ONE ? new ItemStack(ItemsRegistry.MAGIC_CLAY) : glyphPressRecipe.tier == SpellTier.TWO ? new ItemStack(ItemsRegistry.MARVELOUS_CLAY): new ItemStack(ItemsRegistry.MYTHICAL_CLAY);
        List<List<ItemStack>> itemStacks = new ArrayList<>();
        itemStacks.add(Collections.singletonList(clay));
        itemStacks.add(Collections.singletonList(glyphPressRecipe.reagent));
        iIngredients.setInputLists(VanillaTypes.ITEM, itemStacks);
     //   iIngredients.setInput(VanillaTypes.ITEM, glyphPressRecipe.reagent);
        iIngredients.setOutput(VanillaTypes.ITEM, glyphPressRecipe.output);

    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, GlyphPressRecipe o, IIngredients ingredients) {
        int index = 0;
        recipeLayout.getItemStacks().init(index, true, 0, 4);

        recipeLayout.getItemStacks().set(index, ingredients.getInputs(VanillaTypes.ITEM).get(0));

        index++;
        recipeLayout.getItemStacks().init(index, true, 16, 4);
        recipeLayout.getItemStacks().set(index, ingredients.getInputs(VanillaTypes.ITEM).get(1));

        index++;
        recipeLayout.getItemStacks().init(index, true, 64, 4);
        recipeLayout.getItemStacks().set(index, ingredients.getOutputs(VanillaTypes.ITEM).get(0));

    }
}
