package com.hollingsworth.arsnouveau.client.jei;

import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
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

public class EnchantingApparatusRecipeCategory extends MultiInputCategory<EnchantingApparatusRecipe> {

    public IDrawable background;
    public IDrawable icon;

    public EnchantingApparatusRecipeCategory(IGuiHelper helper) {
        super(helper, (enchantingApparatusRecipe -> new MultiProvider(enchantingApparatusRecipe.result, enchantingApparatusRecipe.pedestalItems, enchantingApparatusRecipe.reagent)));
        background = helper.createBlankDrawable(114, 108);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockRegistry.ENCHANTING_APP_BLOCK));
    }

    @Override
    public RecipeType<EnchantingApparatusRecipe> getRecipeType() {
        return JEIArsNouveauPlugin.ENCHANTING_APP_RECIPE_TYPE;
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
        if (recipe.consumesSource())
            renderer.draw(matrixStack, Component.translatable("ars_nouveau.source", recipe.sourceCost), 0.0f, 100f, 10);
    }
}
