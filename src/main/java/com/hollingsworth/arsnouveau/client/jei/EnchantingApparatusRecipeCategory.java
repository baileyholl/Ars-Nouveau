package com.hollingsworth.arsnouveau.client.jei;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.Vec2;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnchantingApparatusRecipeCategory implements IRecipeCategory<EnchantingApparatusRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(ArsNouveau.MODID, "apparatus");

    IGuiHelper helper;
    public IDrawable background;
    public IDrawable icon;
    private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;

    public EnchantingApparatusRecipeCategory(IGuiHelper helper){
        this.helper = helper;
        background = helper.createBlankDrawable(114,108);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,new ItemStack(BlockRegistry.ENCHANTING_APP_BLOCK));
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
    public Class<EnchantingApparatusRecipe> getRecipeClass() {
        return EnchantingApparatusRecipe.class;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("ars_nouveau.enchanting_apparatus");
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
    public void draw(EnchantingApparatusRecipe recipe, @Nonnull IRecipeSlotsView slotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        Font renderer = Minecraft.getInstance().font;
        if(recipe.consumesSource())
            renderer.draw(matrixStack, Component.translatable("ars_nouveau.source", recipe.sourceCost), 0.0f,100f, 10);
    }

    @Override
    public void setIngredients(EnchantingApparatusRecipe o, IIngredients iIngredients) {
        List<List<ItemStack>> itemStacks = new ArrayList<>();
        itemStacks.add(Arrays.asList(o.reagent.getItems()));
        for(Ingredient i : o.pedestalItems){
            itemStacks.add(Arrays.asList(i.getItems()));
        }
        iIngredients.setInputLists(VanillaTypes.ITEM_STACK, itemStacks);
        iIngredients.setOutput(VanillaTypes.ITEM_STACK, o.result);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, EnchantingApparatusRecipe apparatusRecipe, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 48, 45);
        recipeLayout.getItemStacks().set(0, Arrays.asList(apparatusRecipe.reagent.getItems()));

        int index = 1;
        List<List<ItemStack>> pedestalList = ingredients.getInputs(VanillaTypes.ITEM_STACK);
        if(pedestalList.size() > 0) {
            pedestalList = pedestalList.subList(1, pedestalList.size());
            double angleBetweenEach = 360.0 / pedestalList.size();
            Vec2 point = new Vec2(48, 13), center = new Vec2(48, 45);

            for (List<ItemStack> o : pedestalList) {
                recipeLayout.getItemStacks().init(index, true, (int) point.x, (int) point.y);
                recipeLayout.getItemStacks().set(index, o);
                index += 1;
                point = rotatePointAbout(point, center, angleBetweenEach);
            }
        }
        recipeLayout.getItemStacks().init(index, false, 86, 10);
        recipeLayout.getItemStacks().set(index, ingredients.getOutputs(VanillaTypes.ITEM_STACK).get(0));
    }

    public static Vec2 rotatePointAbout(Vec2 in, Vec2 about, double degrees) {
        double rad = degrees * Math.PI / 180.0;
        double newX = Math.cos(rad) * (in.x - about.x) - Math.sin(rad) * (in.y - about.y) + about.x;
        double newY = Math.sin(rad) * (in.x - about.x) + Math.cos(rad) * (in.y - about.y) + about.y;
        return new Vec2((float) newX, (float) newY);
    }
}
