package com.hollingsworth.arsnouveau.client.jei;

import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
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
import org.jetbrains.annotations.NotNull;

public class EnchantingApparatusRecipeCategory<T extends EnchantingApparatusRecipe> extends MultiInputCategory<T> {

    public IDrawable background;
    public IDrawable icon;

    public EnchantingApparatusRecipeCategory(IGuiHelper helper) {
        super(helper, (enchantingApparatusRecipe -> new MultiProvider(enchantingApparatusRecipe.result(), enchantingApparatusRecipe.pedestalItems(), enchantingApparatusRecipe.reagent())));
        background = helper.createBlankDrawable(114, 108);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockRegistry.ENCHANTING_APP_BLOCK));
    }

    @Override
    public RecipeType<RecipeHolder<T>> getRecipeType() {
        //noinspection unchecked
        return (RecipeType<RecipeHolder<T>>) (RecipeType<?>) JEIArsNouveauPlugin.ENCHANTING_APP_RECIPE_TYPE.get();
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
    public void draw(RecipeHolder<T> recipeHolder, @NotNull IRecipeSlotsView slotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        EnchantingApparatusRecipe recipe = recipeHolder.value();
        Font renderer = Minecraft.getInstance().font;
        if (recipe.consumesSource())
            guiGraphics.drawString(renderer, Component.translatable("ars_nouveau.source", recipe.sourceCost()), 0, 100, 10, false);
    }
}
