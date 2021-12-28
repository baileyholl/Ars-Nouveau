package com.hollingsworth.arsnouveau.client.jei;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ImbuementRecipeCategory implements IRecipeCategory<ImbuementRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(ArsNouveau.MODID, "imbuement");

    IGuiHelper helper;
    public IDrawable background;
    public IDrawable icon;
    private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;

    public ImbuementRecipeCategory(IGuiHelper helper){
        this.helper = helper;
        background = helper.createBlankDrawable(60,50);
        icon = helper.createDrawableIngredient(new ItemStack(BlockRegistry.ENCHANTING_APP_BLOCK));
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
    public Class getRecipeClass() {
        return ImbuementRecipe.class;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("block.ars_nouveau.imbuement_chamber");
    }

    @Override
    public IDrawable getBackground() {
        return helper.createBlankDrawable(100,75);
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(ImbuementRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
        IDrawableAnimated arrow = this.cachedArrows.getUnchecked(40);
        arrow.draw( matrixStack,55, 22);
        Font renderer = Minecraft.getInstance().font;
        renderer.draw(matrixStack, new TranslatableComponent("ars_nouveau.source", recipe.source), 0.0f,65f, 10);
    }

    @Override
    public void setIngredients(ImbuementRecipe o, IIngredients iIngredients) {
        List<List<ItemStack>> itemStacks = new ArrayList<>();

        itemStacks.add(Arrays.asList(o.input.getItems()));
        itemStacks.add(Collections.singletonList(o.output));
        for(Ingredient i : o.pedestalItems){

            itemStacks.add(Arrays.asList(i.getItems()));
        }
        iIngredients.setInputLists(VanillaTypes.ITEM, itemStacks);
        //   iIngredients.setInput(VanillaTypes.ITEM, glyphPressRecipe.reagent);
        iIngredients.setOutput(VanillaTypes.ITEM, o.output);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ImbuementRecipe o, IIngredients ingredients) {
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
        }catch (Exception ignored){}

    }
}