package com.hollingsworth.arsnouveau.client.jei;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EnchantingApparatusRecipeCategory implements IRecipeCategory<EnchantingApparatusRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(ArsNouveau.MODID, "apparatus");

    IGuiHelper helper;
    public IDrawable background;
    public IDrawable icon;
    private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;

    public EnchantingApparatusRecipeCategory(IGuiHelper helper){
        this.helper = helper;
        background = helper.createBlankDrawable(60,30);
        icon = helper.createDrawableIngredient(new ItemStack(BlockRegistry.ENCHANTING_APP_BLOCK));
        this.cachedArrows = CacheBuilder.newBuilder()
                .maximumSize(25)
                .build(new CacheLoader<Integer, IDrawableAnimated>() {
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
    public Class getRecipeClass() {
        return EnchantingApparatusRecipe.class;
    }

    @Override
    public String getTitle() {
        return "Enchanting Apparatus";
    }

    @Override
    public IDrawable getBackground() {
        return helper.createBlankDrawable(100,60);
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(EnchantingApparatusRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        IDrawableAnimated arrow = this.cachedArrows.getUnchecked(40);
        arrow.draw( matrixStack,55, 22);
    }

    @Override
    public void setIngredients(EnchantingApparatusRecipe o, IIngredients iIngredients) {
        List<List<ItemStack>> itemStacks = new ArrayList<>();

        itemStacks.add(Arrays.asList(o.reagent.getMatchingStacks()));
        itemStacks.add(Collections.singletonList(o.result));
        for(Ingredient i : o.pedestalItems){

           itemStacks.add(Arrays.asList(i.getMatchingStacks()));
        }
        iIngredients.setInputLists(VanillaTypes.ITEM, itemStacks);
        //   iIngredients.setInput(VanillaTypes.ITEM, glyphPressRecipe.reagent);
        iIngredients.setOutput(VanillaTypes.ITEM, o.result);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, EnchantingApparatusRecipe o, IIngredients ingredients) {
        int index = 0;
        recipeLayout.getItemStacks().init(index, true, 18, 22);

        recipeLayout.getItemStacks().set(index, ingredients.getInputs(VanillaTypes.ITEM).get(0));

        index++;
        recipeLayout.getItemStacks().init(index, true, 80, 22);
        recipeLayout.getItemStacks().set(index, ingredients.getOutputs(VanillaTypes.ITEM).get(0));


//        index++;
//        recipeLayout.getItemStacks().init(index, true, 16, 4);
//        recipeLayout.getItemStacks().set(index, ingredients.getInputs(VanillaTypes.ITEM).get(1));
        try {
            index++;
            recipeLayout.getItemStacks().init(index, true, 0, 4);
            recipeLayout.getItemStacks().set(index, ingredients.getInputs(VanillaTypes.ITEM).get(2));

            index++;
            recipeLayout.getItemStacks().init(index, true, 18, 4);
            recipeLayout.getItemStacks().set(index, ingredients.getInputs(VanillaTypes.ITEM).get(3));

            index++;
            recipeLayout.getItemStacks().init(index, true, 36, 4);
            recipeLayout.getItemStacks().set(index, ingredients.getInputs(VanillaTypes.ITEM).get(4));

            index++;
            recipeLayout.getItemStacks().init(index, true, 0, 22);
            recipeLayout.getItemStacks().set(index, ingredients.getInputs(VanillaTypes.ITEM).get(5));

            index++;
            recipeLayout.getItemStacks().init(index, true, 36, 22);
            recipeLayout.getItemStacks().set(index, ingredients.getInputs(VanillaTypes.ITEM).get(6));
            index++;
            recipeLayout.getItemStacks().init(index, true, 0, 40);
            recipeLayout.getItemStacks().set(index, ingredients.getInputs(VanillaTypes.ITEM).get(7));
            index++;
            recipeLayout.getItemStacks().init(index, true, 18, 40);
            recipeLayout.getItemStacks().set(index, ingredients.getInputs(VanillaTypes.ITEM).get(8));
            index++;
            recipeLayout.getItemStacks().init(index, true, 36, 40);
            recipeLayout.getItemStacks().set(index, ingredients.getInputs(VanillaTypes.ITEM).get(9));
        }catch (Exception e){}

    }
}
