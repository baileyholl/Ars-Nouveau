package com.hollingsworth.arsnouveau.client.jei;

import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
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

public class ImbuementRecipeCategory extends MultiInputCategory<ImbuementRecipe> {


    public IDrawable background;
    public IDrawable icon;

    public ImbuementRecipeCategory(IGuiHelper helper) {
        super(helper, (imbuementRecipe -> new MultiProvider(imbuementRecipe.output, imbuementRecipe.pedestalItems, imbuementRecipe.input)));
        background = helper.createBlankDrawable(114,108);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockRegistry.IMBUEMENT_BLOCK));
    }

    @Override
    public RecipeType<ImbuementRecipe> getRecipeType() {
        return JEIArsNouveauPlugin.IMBUEMENT_RECIPE_TYPE;
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
    public void draw(ImbuementRecipe recipe, @Nonnull IRecipeSlotsView slotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        Font renderer = Minecraft.getInstance().font;
        renderer.draw(matrixStack, Component.translatable("ars_nouveau.source", recipe.source), 0.0f, 100f, 10);
    }
}
