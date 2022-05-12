package com.hollingsworth.arsnouveau.client.jei;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CrushRecipe;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectCrush;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CrushRecipeCategory implements IRecipeCategory<CrushRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(ArsNouveau.MODID, "crush");

    IGuiHelper helper;
    public IDrawable background;
    public IDrawable icon;
    public List<CrushRecipe.CrushOutput> outputs = new ArrayList<>();
    private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;

    public CrushRecipeCategory(IGuiHelper helper){
        this.helper = helper;
        background = helper.createBlankDrawable(60,50);
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
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends CrushRecipe> getRecipeClass() {
        return CrushRecipe.class;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("ars_nouveau.crush_recipe");
    }

    @Override
    public IDrawable getBackground() {
        return helper.createBlankDrawable(120,8 + 16 * outputs.size());
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
    public void setIngredients(CrushRecipe o, IIngredients iIngredients) {
        List<List<ItemStack>> inputs = new ArrayList<>();
        inputs.add( Arrays.asList(o.input.getItems()));
        iIngredients.setInputLists(VanillaTypes.ITEM,inputs);
        iIngredients.setOutputs(VanillaTypes.ITEM, o.outputs.stream().map(c -> c.stack).collect(Collectors.toList()));
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CrushRecipe o, IIngredients ingredients) {
        int index = 0;
        this.outputs = o.outputs;
        recipeLayout.getItemStacks().init(index, true, 6, 5);
        recipeLayout.getItemStacks().set(index, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        index++;

        for(int i = 0; i < ingredients.getOutputs(VanillaTypes.ITEM).size(); i++){
            recipeLayout.getItemStacks().init(index, true, 60, 5+ 16 * i);
            recipeLayout.getItemStacks().set(index, ingredients.getOutputs(VanillaTypes.ITEM).get(i));
            index++;
        }
    }
}
